package top.wetabq.wac.logging.module

import cn.nukkit.Player
import cn.nukkit.utils.LogLevel
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.logging.WACLog
import top.wetabq.wac.logging.reg.RegLogModule
import top.wetabq.wac.module.ModuleVersion
import top.wetabq.wac.module.exception.ModuleNotRegisterException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class CheckLog : WACLog() {

    override fun <T> register(registry: T) {
        super.register(registry)
        val defaultConfig = WAntiCheatPro.df
        if(registry is RegLogModule) {
            registry.setConsoleLog(defaultConfig.defaultConfig[ConfigPaths.LOGGING_CHECKLOG_CONSOLELOG].toString().toBoolean())
                    .setLogLevel(LogLevel.INFO)
                    .setLogPrefix(defaultConfig.defaultConfig[ConfigPaths.LOGGING_CHECKLOG_PREFIX].toString())
                    .setPrintTime(defaultConfig.defaultConfig[ConfigPaths.LOGGING_CHECKLOG_PRINTTIME].toString().toBoolean())
                    .setRecordLog(defaultConfig.defaultConfig[ConfigPaths.LOGGING_CHECKLOG_RECORDLOG].toString().toBoolean())
                    .setModuleName("CheckLog")
                    .setModuleAuthor("WetABQ")
                    .setModuleVersion(ModuleVersion(1,0,0,1))
                    .context()
        }
    }

    fun checkLog(player: Player,checkType: CheckType,reason: String) {
        log("[Type:${checkType.getName()} Reason:$reason] Checked player[${player.displayName}] try cheating at [${player.level.folderName}]")
    }

}