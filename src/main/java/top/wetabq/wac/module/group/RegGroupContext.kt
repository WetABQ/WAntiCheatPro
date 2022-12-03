package top.wetabq.wac.module.group

import cn.nukkit.event.Listener
import cn.nukkit.scheduler.AsyncTask
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.config.WConfig
import top.wetabq.wac.module.reg.RegisterContext

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegGroupContext : RegisterContext() {

    val registeredListener = ArrayList<Listener>()
    var bindConfig: WConfig? = null
    var configPaths = ArrayList<String>()
    val bindCommands = ArrayList<WSubCommand>()
    var commandPrefix: String = WAntiCheatPro.TITLE
    val asyncTasks = ArrayList<AsyncTask>()

}