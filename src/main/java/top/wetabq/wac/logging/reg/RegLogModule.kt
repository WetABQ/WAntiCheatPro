package top.wetabq.wac.logging.reg

import cn.nukkit.utils.LogLevel
import top.wetabq.wac.logging.WACLog
import top.wetabq.wac.module.reg.RegisterModule
import java.lang.RuntimeException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegLogModule(private val module: WACLog) : RegisterModule(module) {

    init {
        context = RegLogContext()
    }

    fun setRecordLog(recordlog: Boolean): RegLogModule {
        if (context is RegLogContext) (context as RegLogContext).recordLog = recordlog
        return this
    }

    fun setConsoleLog(consoleLog: Boolean): RegLogModule {
        if (context is RegLogContext) (context as RegLogContext).consoleLog = consoleLog
        return this
    }

    fun setLogLevel(logLevel: LogLevel): RegLogModule {
        if (context is RegLogContext) (context as RegLogContext).logLevel = logLevel
        return this
    }

    fun setLogPrefix(logPrefix: String): RegLogModule {
        if (context is RegLogContext) (context as RegLogContext).logPrefix = logPrefix
        return this
    }

    fun setPrintTime(printTime: Boolean): RegLogModule {
        if (context is RegLogContext) (context as RegLogContext).printTime = printTime
        return this
    }

    override fun context() {
        module.context(if (context is RegLogContext){(context as RegLogContext)} else { throw RuntimeException("moduleContext type error")})
    }

}