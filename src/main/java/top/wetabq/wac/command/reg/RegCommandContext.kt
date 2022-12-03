package top.wetabq.wac.command.reg

import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.module.reg.RegisterContext

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegCommandContext : RegisterContext() {

    val bindCommands = ArrayList<WSubCommand>()
    var commandPrefix: String = WAntiCheatPro.TITLE

}