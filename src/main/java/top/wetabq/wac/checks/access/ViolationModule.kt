package top.wetabq.wac.checks.access

import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerChatEvent
import cn.nukkit.event.player.PlayerPreLoginEvent
import cn.nukkit.utils.TextFormat
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.utils.DateUtils
import top.wetabq.wac.command.CommandArgument
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.WConfig
import top.wetabq.wac.config.exception.ConfigLoadingException
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.ModuleVersion
import top.wetabq.wac.module.group.GroupModule
import top.wetabq.wac.module.group.RegGroupContext
import top.wetabq.wac.module.group.RegGroupModule
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
fun Int.standardActionStatus(): String = if (this == 1) "正常" else "数据库错误"

class ViolationModule : GroupModule() {

    companion object {
        var violationConfig = HashMap<String, ArrayList<ViolationData>>()
        var violationBuffer = HashMap<String, ArrayList<ViolationProcess<*>>>()
        private var helperList = ArrayList<String>()
        const val mysqlMode = false

        @JvmStatic
        fun translateMessage(str: String, violationData: ViolationData): String {
            var str0 = WAntiCheatPro.translateMessage(str)
            str0 = str0.replace("{extra}", violationData.extra)
            str0 = str0.replace("{duration}", violationData.violationDuration.toString())
            str0 = str0.replace("{vid}", violationData.violationId)
            str0 = str0.replace(
                "{endDuration}",
                let {
                    if (violationData.violationType == ViolationType.MUTE) DateUtils.getDateStr(
                        DateUtils.getEndSDate(
                            DateUtils.getStartDate(violationData.time),
                            violationData.violationDuration
                        )
                    ) else DateUtils.getDateStr(
                        DateUtils.getEndDate(
                            DateUtils.getStartDate(violationData.time),
                            violationData.violationDuration
                        )
                    )
                })
            return TextFormat.colorize(str0)
        }

        private fun hasProcessViolationPermission(sender: CommandSender): Boolean {
            return (helperList.isNotEmpty() && helperList.contains(sender.name)) ||
                    (helperList.isEmpty() && sender.isOp) ||
                    !sender.isPlayer
        }


    }

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.bindConfig(object : WConfig("violationPlayer") {
                init {
                    init()
                }

                override fun init() {
                    if (!mysqlMode) {
                        if (!isEmpty()) {
                            try {
                                val violationConfigStringMap =
                                    configSection["violationHistory"] as HashMap<String, ArrayList<Map<String, String>>>
                                violationConfigStringMap.forEach { k, v ->
                                    v.forEach { vd ->
                                        violationConfig[k]?.add(
                                            ViolationData(
                                                vd["violationId"].toString(),
                                                vd["violationDuration"]?.toInt()
                                                    ?: 0,
                                                ViolationType.fromTypeName(vd["violationType"] ?: "")
                                                    ?: ViolationType.NONE,
                                                CheckType.fromTypeName(vd["checkType"] ?: "") ?: CheckType.ALL,
                                                vd["time"]
                                                    ?: "1990/1/1 23:59:59",
                                                vd["extra"] ?: ""
                                            )
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                throw ConfigLoadingException("can't init violationPlayer.yml config", context)
                            }
                        } else {
                            spawnDefaultConfig()
                        }
                    }
                }

                override fun spawnDefaultConfig() {
                    if (!mysqlMode) {
                        if (isEmpty()) {
                            configSection["violationHistory"] = hashMapOf<String, Map<String, String>>()
                        }
                        init()
                        save()
                    }
                }

                override fun save() {
                    if (!mysqlMode) {
                        if (!isEmpty()) {
                            try {
                                configSection.clear()
                                val violationMapString = hashMapOf<String, ArrayList<Map<String, String>>>()
                                for ((k, v) in violationConfig) {
                                    for (vd in v) {
                                        (violationMapString[k] ?: let {
                                            violationMapString[k] = arrayListOf()
                                            violationMapString[k]!!
                                        }).add(vd.toMap())
                                    }
                                }
                                configSection["violationHistory"] = violationMapString
                                config.setAll(configSection)
                                config.save()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                throw ConfigLoadingException("can't save violationPlayer.yml config", context)
                            }
                        } else {
                            spawnDefaultConfig()
                        }
                    }
                }
            })
                .addConfigPath(ConfigPaths.WACTITLE, "&6&lWAC &r&c» ")
                .addConfigPath(ConfigPaths.VIOLATION_DURATION_MUTE, 5)
                .addConfigPath(ConfigPaths.VIOLATION_DURATION_BAN, 7)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_WARNING, true)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_SETBACK, true)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_KICK, true)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_MUTE, true)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_MUTE_AUTODELETELOG, true)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_BAN, true)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_BANWAVE, true)
                .addConfigPath(ConfigPaths.VIOLATION_SWITCH_LOGRECORD, true)
                .addConfigPath(ConfigPaths.VIOLATION_BUFFER_AUTO_SIZE, 5)
                .addConfigPath(
                    ConfigPaths.VIOLATION_MESSAGE_WARNING,
                    "{WACTitle}[WARNING ExtraLog:{extra}] &6&lPlease pay attention to your game behavior"
                )
                .addConfigPath(
                    ConfigPaths.VIOLATION_MESSAGE_KICK,
                    "&c&l[WAC DETECT KICK]{enter}&6&lPlease pay attention to your game behavior{enter}&bExtraLog:{extra}{enter}VID:{vid}"
                )
                .addConfigPath(
                    ConfigPaths.VIOLATION_MESSAGE_MUTE_0,
                    "{WACTitle}[MUTE DURATION:{duration}s] &6&lPlease pay attention to your game behavior"
                )
                .addConfigPath(
                    ConfigPaths.VIOLATION_MESSAGE_MUTE_1,
                    "{WACTitle}[MUTE EndTime:{endDuration}] &6&lYou are muted"
                )
                .addConfigPath(
                    ConfigPaths.VIOLATION_MESSAGE_BAN, "&c&l[WAC DETECT BAN] &r&e&oUnbanTime:{endDuration}{enter}" +
                            "&6&lPlease pay attention to your game behavior{enter}" +
                            "&bExtraLog:{extra}{enter}&l&aVID:{vid}"
                )
                .addConfigPath(
                    ConfigPaths.VIOLATION_MESSAGE_BAN_BROADCAST,
                    "{WACTitle}&lA player was kicked out of the game because of abuse of server features or cheating"
                )
                .bindListener(object : Listener {

                    @EventHandler
                    fun onPlayerPreLoginEvent(event: PlayerPreLoginEvent) {
                        val defaultConfigModule = WAntiCheatPro.df
                        val player = event.player
                        val violationDataList = violationConfig[player.name]
                        val defaultConfig = defaultConfigModule.defaultConfig
                        for (vd in violationDataList ?: arrayListOf()) {
                            if (vd.violationType == ViolationType.BAN) {
                                if (DateUtils.isEffectiveDate(
                                        Date(),
                                        DateUtils.getStartDate(vd.time),
                                        DateUtils.getEndDate(DateUtils.getStartDate(vd.time), vd.violationDuration)
                                    )
                                ) {
                                    player.kick(
                                        translateMessage(
                                            defaultConfig[ConfigPaths.VIOLATION_MESSAGE_BAN].toString(),
                                            vd
                                        ), false
                                    )
                                }
                            }
                        }
                    }

                    @EventHandler
                    fun onPlayerChat(event: PlayerChatEvent) {
                        val defaultConfigModule = WAntiCheatPro.df
                        val player = event.player
                        val violationDataList = violationConfig[player.name]
                        val defaultConfig = defaultConfigModule.defaultConfig
                        val willDeleteList = ArrayList<ViolationData>() //待删除元素表
                        for (vd in violationDataList ?: arrayListOf()) {
                            if (vd.violationType == ViolationType.MUTE) {
                                if (DateUtils.isEffectiveDate(
                                        Date(),
                                        DateUtils.getStartDate(vd.time),
                                        DateUtils.getEndSDate(DateUtils.getStartDate(vd.time), vd.violationDuration)
                                    )
                                ) {
                                    // 如果在区间时间内
                                    player.sendMessage(
                                        translateMessage(
                                            defaultConfig[ConfigPaths.VIOLATION_MESSAGE_MUTE_1].toString(),
                                            vd
                                        )
                                    )
                                } else if (defaultConfig[ConfigPaths.VIOLATION_SWITCH_MUTE_AUTODELETELOG].toString().toBoolean()) willDeleteList.add(
                                    vd
                                )
                            }
                        }
                        for (vd in willDeleteList) violationDataList?.remove(vd)

                    }

                })
                .addCommand(object: WSubCommand("unban") {
                    override fun getArguments(): Array<CommandArgument> {
                        return arrayOf(
                            CommandArgument("violationId", "string",false),
                            CommandArgument("playerName", "rawtext",false)
                        )
                    }

                    override fun getAliases(): Array<String> {
                        return arrayOf("ub")
                    }

                    override fun getDescription(): String {
                        return "pardon player for ban"
                    }

                    override fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean {
                        if (hasProcessViolationPermission(sender)) {
                            val violationData =
                                (violationConfig.clone() as HashMap<String, ArrayList<ViolationData>>)[args[2]]?.lastOrNull { it.violationId == args[1] }
                            return if (violationData is ViolationData) {
                                violationConfig[args[2]]?.remove(violationData)
                                (getModuleContext() as RegGroupContext?)?.bindConfig?.save()
                                sender.sendMessage(WAntiCheatPro.TITLE + "操作成功")
                                true
                            } else {
                                sender.sendMessage(WAntiCheatPro.TITLE + "找不到 violationId = ${args[1]} player = ${args[2]} 配对的违规处理记录")
                                false
                            }
                        } else {
                            sender.sendMessage(WAntiCheatPro.TITLE + "权限不足")
                            return false
                        }
                    }

                })
                .addCommand(object: WSubCommand("processPlayer") {
                    override fun getArguments(): Array<CommandArgument> {
                        return arrayOf(
                            CommandArgument("playerName", "rawtext",false)
                        )
                    }

                    override fun getAliases(): Array<String> {
                        return arrayOf("pp", "process")
                    }

                    override fun getDescription(): String {
                        return "process player in violation buffer"
                    }

                    override fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean {
                        if (hasProcessViolationPermission(sender)) {
                            violationBuffer[args[1]]?.lastOrNull()?.executePunishment()
                            violationBuffer.remove(args[1])
                            sender.sendMessage(WAntiCheatPro.TITLE + "操作成功")
                        } else {
                            sender.sendMessage(WAntiCheatPro.TITLE + "权限不足")
                        }
                        return true
                    }

                })
                .addCommand(object: WSubCommand("cleanPlayerBuffer") {
                    override fun getArguments(): Array<CommandArgument> {
                        return arrayOf(
                            CommandArgument("playerName", "rawtext",false)
                        )
                    }

                    override fun getAliases(): Array<String> {
                        return arrayOf("cpb", "cleanpb", "cpbuffer")
                    }

                    override fun getDescription(): String {
                        return "clean player violation buffer"
                    }

                    override fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean {
                        if (hasProcessViolationPermission(sender)) {
                            violationBuffer.remove(args[1])
                            sender.sendMessage(WAntiCheatPro.TITLE + "操作成功")
                        } else {
                            sender.sendMessage(WAntiCheatPro.TITLE + "权限不足")
                        }
                        return true
                    }

                })
                .addCommand(object: WSubCommand("showBuffer") {
                    override fun getArguments(): Array<CommandArgument> {
                        return arrayOf()
                    }

                    override fun getAliases(): Array<String> {
                        return arrayOf("sb", "show", "view")
                    }

                    override fun getDescription(): String {
                        return "view the violation buffer"
                    }

                    override fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean {
                        if (hasProcessViolationPermission(sender)) {
                            sender.sendMessage("${TextFormat.YELLOW}----WAC Violation Buffer----")
                            violationBuffer.forEach { (name, violationProcessList) ->
                                sender.sendMessage("${TextFormat.RED}Player: $name -> {")
                                violationProcessList.forEach { process ->
                                    sender.sendMessage("----> ${process.getCheck().getCheckType()} - ${process.violationType}")
                                }
                                sender.sendMessage("${TextFormat.GOLD}-----------")
                            }
                            sender.sendMessage("${TextFormat.YELLOW}----WAC Violation Buffer----")
                        } else {
                            sender.sendMessage(WAntiCheatPro.TITLE + "权限不足")
                        }
                        return true
                    }

                })
                .registerCommands()
                .setModuleName("ViolationProcess")
                .setModuleAuthor("WetABQ")
                .setModuleVersion(ModuleVersion(1, 0, 0, 1))
                .context()
            WAntiCheatPro.TITLE = TextFormat.colorize(WAntiCheatPro.df.defaultConfig[ConfigPaths.WACTITLE].toString())
        }
    }
}