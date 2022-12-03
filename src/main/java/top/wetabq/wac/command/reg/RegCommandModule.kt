package top.wetabq.wac.command.reg

import top.wetabq.wac.command.WACommand
import top.wetabq.wac.command.WCommand
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.module.reg.RegisterModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegCommandModule(private val module: WACommand) : RegisterModule(module) {

    init {
        context = RegCommandContext()
    }

    fun addCommand(subCommand: WSubCommand) : RegCommandModule {
        (context as RegCommandContext).bindCommands.add(subCommand)
        return this
    }

    fun setCommandPrefix(prefix: String) : RegCommandModule {
        (context as RegCommandContext).commandPrefix = prefix
        return this
    }

    fun registerCommands() : RegCommandModule { //Must use
        (context as RegCommandContext).bindCommands.forEach { cmd -> WCommand.subCommand.add(cmd) }
        return this
    }

    override fun context() {
        module.context(context as RegCommandContext)
    }


}