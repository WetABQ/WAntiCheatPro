package top.wetabq.wac.module

import cn.nukkit.Server
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.access.ViolationModule
import top.wetabq.wac.checks.block.blockbreak.BlockBreakModule
import top.wetabq.wac.checks.block.blockbreak.checks.FastBreak
import top.wetabq.wac.checks.block.blockbreak.checks.HiddenMine
import top.wetabq.wac.checks.block.blockbreak.checks.ReachBreak
import top.wetabq.wac.checks.block.blockbreak.checks.WrongBreak
import top.wetabq.wac.checks.block.blockplace.BlockPlaceModule
import top.wetabq.wac.checks.block.blockplace.checks.PlaceReach
import top.wetabq.wac.checks.block.blockplace.checks.WrongPlace
import top.wetabq.wac.checks.chat.ChatCheckModule
import top.wetabq.wac.checks.chat.checks.ColorChat
import top.wetabq.wac.checks.chat.checks.FastChat
import top.wetabq.wac.checks.chat.checks.FastCommand
import top.wetabq.wac.checks.extra.BanWaveModule
import top.wetabq.wac.checks.extra.FakeBotModule
import top.wetabq.wac.checks.fight.FightCheckModule
import top.wetabq.wac.checks.fight.checks.AutoAim
import top.wetabq.wac.checks.fight.checks.Critical
import top.wetabq.wac.checks.fight.checks.FightReach
import top.wetabq.wac.checks.fight.checks.WrongAttack
import top.wetabq.wac.checks.movement.MovingCheckModule
import top.wetabq.wac.checks.movement.checks.*
import top.wetabq.wac.command.WACommand
import top.wetabq.wac.command.WCommand
import top.wetabq.wac.command.normal.HelpCommand
import top.wetabq.wac.command.normal.VersionCommand
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.config.reg.RegConfigContext
import top.wetabq.wac.logging.module.CheckLog
import top.wetabq.wac.logging.module.DebugLog
import top.wetabq.wac.logging.module.LogRecordConfig
import top.wetabq.wac.module.exception.ModuleNotRegisterException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class WACModuleManager {

    private val moduleList = HashMap<String,WACModule<*,*>>()

    init {
        WAntiCheatPro.instance.logger.notice("WACModuleManager Enable,start loading modules")
    }


    fun registerModule(module: WACModule<*,*>) {
        module.register(module.getRegisterInstance())
        WAntiCheatPro.instance.logger.notice("Add module ${module.getModuleContext().moduleName}_${module.getModuleContext().moduleVersion} by ${module.getModuleContext().moduleAuthor}")
        moduleList[module.getModuleContext().moduleName] = module
    }

    fun registerPriorityLoading(){
        registerModule(DefaultConfig())
        registerModule(DebugLog())
    }

    fun registerAllDefaultModule(){
        registerModule(CheckLog())
        registerModule(LogRecordConfig())
        registerModule(HelpCommand())
        registerModule(VersionCommand())
        registerModule(ViolationModule())
        registerModule(BanWaveModule())
        registerModule(BlockBreakModule())
        registerModule(FastBreak())
        registerModule(ReachBreak())
        registerModule(WrongBreak())
        registerModule(BlockPlaceModule())
        registerModule(PlaceReach())
        registerModule(WrongPlace())
        registerModule(ChatCheckModule())
        registerModule(ColorChat())
        registerModule(FastChat())
        registerModule(FastCommand())
        registerModule(FightCheckModule())
        registerModule(AutoAim())
        registerModule(Critical())
        registerModule(FightReach())
        registerModule(WrongAttack())
        registerModule(MovingCheckModule())
        registerModule(HighJump())
        registerModule(MovingFly())
        registerModule(NoFall())
        registerModule(Speed())
        registerModule(ThroughWall())
        registerModule(HiddenMine())
        registerModule(FakeBotModule())
        //registerModule(TestListener())
        addAllCheckDefaultConfigPath()
        if (WACommand.wCommand == null) { // Server layer - register command
            WACommand.wCommand = WCommand()
            Server.getInstance().commandMap.register( "", WACommand.wCommand)
        }
    }

    fun addAllCheckDefaultConfigPath() {
        moduleList.values.forEach { mod ->
            if (Check::class.java.isAssignableFrom(mod.javaClass) && (mod as Check<*>).getCheckType().getParent() != CheckType.ALL) {
                val defaultConfig = (moduleList["DefaultConfig"] as DefaultConfig).defaultConfig
                if (!defaultConfig.containsKey(ConfigPaths.CHECKS+mod.javaClass.name.toLowerCase()+ConfigPaths.CHECKS_SWITCH)) {
                    defaultConfig[ConfigPaths.CHECKS + mod.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_SWITCH] = true
                    defaultConfig[ConfigPaths.CHECKS + mod.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT] = true
                    defaultConfig[ConfigPaths.CHECKS + mod.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_WARNINGVL] = 10
                    defaultConfig[ConfigPaths.CHECKS + mod.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_MUTEVL] = -1
                    defaultConfig[ConfigPaths.CHECKS + mod.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_KICKVL] = -1
                    defaultConfig[ConfigPaths.CHECKS + mod.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_BANVL] = 100
                }
            }
        }
        (moduleList["DefaultConfig"]?.getModuleContext() as RegConfigContext).bindConfig?.save()
    }

    fun disableAllModule() {
        for (mod in moduleList.values) mod.disable()
    }


    fun getModule(moduleName: String): WACModule<*,*>? {
        if (moduleList.isNotEmpty()) {
            return moduleList[moduleName]
        } else throw ModuleNotRegisterException("no module register when @call getModule")
    }

}