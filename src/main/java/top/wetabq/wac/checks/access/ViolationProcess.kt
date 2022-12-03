package top.wetabq.wac.checks.access

import cn.nukkit.Player
import cn.nukkit.Server
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.event.PlayerViolationEvent
import top.wetabq.wac.checks.extra.BanWaveModule
import top.wetabq.wac.checks.utils.DateUtils
import top.wetabq.wac.checks.utils.RandomUtils
import top.wetabq.wac.checks.utils.StrUtils
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.logging.module.CheckLog
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupContext
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class ViolationProcess<out D: CheckData>(private val check: Check<D>, private val player: Player,
    val violationType: ViolationType, private val extra:String) : IViolationInfo<D> {

    private var cancel = false
    private var isBanWave = false
    private var executed = false

    override fun getCheck(): Check<*> {
        return check
    }

    override fun getCheckData(): D {
        return check.getPlayerCheckData(player) as D
    }

    override fun getExtra(): String {
        return extra
    }

    override fun executePunishment() {
        executed = true
        isBanWave = false
        val checkLog = WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.CHECKLOG) as CheckLog?
        val defaultConfigModule = WAntiCheatPro.df
        if (cancel) return
        val defaultConfig = defaultConfigModule.defaultConfig
        if (checkLog is CheckLog && defaultConfig[ConfigPaths.VIOLATION_SWITCH_LOGRECORD].toString().toBoolean()) checkLog.checkLog(player, check.getCheckType(), "[WAC DETECT] $extra")
        if (!ViolationModule.violationConfig.containsKey(player.name)) ViolationModule.violationConfig[player.name] = arrayListOf()
        val violationData = ViolationData(
            StrUtils.getMd5(player.rawHashCode().toString() + Random().nextLong().toString()), 0,
                ViolationType.NONE, check.getCheckType(), DateUtils.getNowDateStr(), extra)
        when (violationType) {
            ViolationType.WARNING -> {
                if (defaultConfig[ConfigPaths.VIOLATION_SWITCH_WARNING].toString().toBoolean()) {
                    violationData.violationType = ViolationType.WARNING
                    val playerViolationEvent = PlayerViolationEvent(player,violationData)
                    Server.getInstance().pluginManager.callEvent(playerViolationEvent)
                    if (playerViolationEvent.isCancelled) return
                    if (player.isOnline) player.sendMessage(ViolationModule.translateMessage(defaultConfig[ConfigPaths.VIOLATION_MESSAGE_WARNING].toString(),violationData))
                    ViolationModule.violationConfig[player.name]?.add(violationData)
                }
            }
            ViolationType.SETBACK -> {
                if (defaultConfig[ConfigPaths.VIOLATION_SWITCH_SETBACK].toString().toBoolean()) {
                    violationData.violationType = ViolationType.SETBACK
                    val playerViolationEvent = PlayerViolationEvent(player,violationData)
                    Server.getInstance().pluginManager.callEvent(playerViolationEvent)
                    if (playerViolationEvent.isCancelled) return
                    if (player.isOnline) getCheckData().setBack()
                    ViolationModule.violationConfig[player.name]?.add(violationData)
                }
            }
            ViolationType.MUTE -> {
                if (defaultConfig[ConfigPaths.VIOLATION_SWITCH_MUTE].toString().toBoolean()) {
                    violationData.violationDuration = defaultConfig[ConfigPaths.VIOLATION_DURATION_MUTE].toString().toInt()
                    violationData.violationType = ViolationType.MUTE
                    val playerViolationEvent = PlayerViolationEvent(player,violationData)
                    Server.getInstance().pluginManager.callEvent(playerViolationEvent)
                    if (playerViolationEvent.isCancelled) return
                    player.sendMessage(ViolationModule.translateMessage(defaultConfig[ConfigPaths.VIOLATION_MESSAGE_MUTE_0].toString(),violationData))
                    ViolationModule.violationConfig[player.name]?.add(violationData)
                }
            }
            ViolationType.KICK -> {
                if (defaultConfig[ConfigPaths.VIOLATION_SWITCH_KICK].toString().toBoolean()) {
                    violationData.violationType = ViolationType.KICK
                    val playerViolationEvent = PlayerViolationEvent(player,violationData)
                    Server.getInstance().pluginManager.callEvent(playerViolationEvent)
                    if (playerViolationEvent.isCancelled) return
                    if (player.isOnline) player.kick(ViolationModule.translateMessage(defaultConfig[ConfigPaths.VIOLATION_MESSAGE_KICK].toString(),violationData),false)
                    Server.getInstance().broadcastMessage(WAntiCheatPro.translateMessage(defaultConfig[ConfigPaths.VIOLATION_MESSAGE_BAN_BROADCAST].toString()))
                    ViolationModule.violationConfig[player.name]?.add(violationData)
                }
            }
            ViolationType.BAN -> {
                if (defaultConfig[ConfigPaths.VIOLATION_SWITCH_BAN].toString().toBoolean()) {
                    violationData.violationType = ViolationType.BAN
                    violationData.violationDuration = defaultConfig[ConfigPaths.VIOLATION_DURATION_BAN].toString().toInt()
                    val playerViolationEvent = PlayerViolationEvent(player,violationData)
                    Server.getInstance().pluginManager.callEvent(playerViolationEvent)
                    if (playerViolationEvent.isCancelled) return
                    if (player.isOnline) player.kick(ViolationModule.translateMessage(defaultConfig[ConfigPaths.VIOLATION_MESSAGE_BAN].toString(),violationData),false)
                    Server.getInstance().broadcastMessage(WAntiCheatPro.translateMessage(defaultConfig[ConfigPaths.VIOLATION_MESSAGE_BAN_BROADCAST].toString()))
                    ViolationModule.violationConfig[player.name]?.add(violationData)
                }
            }
            else -> throw RuntimeException("Unknown violation type")
        }
        val violationModule = WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.VIOLATIONMODULE) as ViolationModule?
        (violationModule?.getModuleContext() as RegGroupContext?)?.bindConfig?.save()
    }

    override fun executeInBanWave() {
        isBanWave = true
        val defaultConfigModule = WAntiCheatPro.df
        val defaultConfig = defaultConfigModule.defaultConfig

        if (defaultConfig[ConfigPaths.VIOLATION_SWITCH_BANWAVE].toString().toBoolean()) {
            BanWaveModule.banWave[this] = RandomUtils.r(defaultConfig[ConfigPaths.VIOLATION_DURATION_BANWAVE_MIN].toString().toInt(),
                    defaultConfig[ConfigPaths.VIOLATION_DURATION_BANWAVE_MAX].toString().toInt())
        } else if (violationType == ViolationType.BAN || violationType == ViolationType.KICK) {
            if (!ViolationModule.violationBuffer.containsKey(player.name)) {
                ViolationModule.violationBuffer[player.name] = ArrayList()
            }
            WAntiCheatPro.instance.logger.info(WAntiCheatPro.TITLE + "将 ${player.name} 加入了 WAC 违规处理缓冲器")
            ViolationModule.violationBuffer[player.name]?.add(this)
            if ((ViolationModule.violationBuffer[player.name]?.size
                    ?: 0) > defaultConfig[ConfigPaths.VIOLATION_BUFFER_AUTO_SIZE].toString().toInt()
            ) {
                WAntiCheatPro.instance.logger.info(WAntiCheatPro.TITLE + "玩家 ${player.name} 超过了 WAC 违规处理缓冲器最大次数, 已经自动处理!")
                executePunishment()
            }
        } else {
            executePunishment()
        }
    }

    override fun isBanWave(): Boolean {
        return isBanWave
    }

    override fun forceCancel() {
        cancel = true
    }

    override fun preventCancel() {
        cancel = false
    }

    override fun willCancel(): Boolean {
        return cancel
    }

}