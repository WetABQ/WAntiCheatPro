package top.wetabq.wac.logging

import top.wetabq.wac.logging.module.LogRecordConfig
import top.wetabq.wac.logging.reg.RegLogContext
import top.wetabq.wac.logging.reg.RegLogModule
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.WACModule
import top.wetabq.wac.module.exception.ModuleContextException
import top.wetabq.wac.module.exception.RegModuleException
import java.text.SimpleDateFormat
import java.util.*

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class WACLog : WACModule<RegLogModule, RegLogContext>() {

    override fun getRegisterInstance(): RegLogModule {
        return RegLogModule(this)
    }

    override fun context(registerContext: RegLogContext) {
        context = registerContext
    }

    override fun <T> register(registry: T) {
        if (registry is RegLogModule) else throw RegModuleException(this,"registry isn't RegLogModule")
    }

    fun log(logMessage: String) {
        if (context is RegLogContext) {
            val c = (context as RegLogContext)
            if (c.consoleLog) getWACInstance().logger.log(c.logLevel, c.logPrefix + logMessage)
            if (c.recordLog) {
                val logRecordConfig = getWACInstance().moduleManager.getModule(DefaultModuleName.LOGRECORDCONFIG) as LogRecordConfig
                var time = ""
                if (c.printTime) time = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date())
                logRecordConfig.addLogRecord("[$time][${c.logLevel}][${c.logPrefix}] $logMessage")
            }
        } else {
            throw ModuleContextException(this,"context isn't RegLogContext")
        }
    }

}