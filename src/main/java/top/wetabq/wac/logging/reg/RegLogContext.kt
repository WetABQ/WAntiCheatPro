package top.wetabq.wac.logging.reg

import cn.nukkit.utils.LogLevel
import top.wetabq.wac.module.reg.RegisterContext

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegLogContext : RegisterContext() {

    var recordLog = true
    var consoleLog = true
    var logLevel = LogLevel.INFO
    var logPrefix = "None"
    var printTime = false

}

