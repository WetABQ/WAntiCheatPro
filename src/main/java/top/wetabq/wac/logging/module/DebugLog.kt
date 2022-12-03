package top.wetabq.wac.logging.module

import cn.nukkit.Player
import cn.nukkit.utils.LogLevel
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.logging.WACLog
import top.wetabq.wac.logging.reg.RegLogContext
import top.wetabq.wac.logging.reg.RegLogModule
import top.wetabq.wac.module.ModuleVersion
import top.wetabq.wac.module.debug.IDebugPlayer
import top.wetabq.wac.module.exception.ModuleNotRegisterException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class DebugLog : WACLog(),IDebugPlayer {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegLogModule) {
            registry.setPrintTime(false)
                    .setLogPrefix("WACDebug")
                    .setLogLevel(LogLevel.DEBUG)
                    .setConsoleLog(true)
                    .setRecordLog(false)
                    .setModuleName("DebugLog")
                    .setModuleAuthor("WetABQ")
                    .setModuleVersion(ModuleVersion(1,0,0,1))
                    .context()
        }
    }

    override fun debug(player: Player, message: String) {
        val defaultConfigModule = WAntiCheatPro.df
        if (defaultConfigModule.defaultConfig[ConfigPaths.DEBUGMODE].toString().toBoolean()) {
            log("player=[${player.name}]"+message)
            player.sendMessage((context as RegLogContext).logPrefix + " " + message)
        }
    }

}