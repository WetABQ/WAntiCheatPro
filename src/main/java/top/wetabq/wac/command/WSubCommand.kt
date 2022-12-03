package top.wetabq.wac.command

import cn.nukkit.command.CommandSender
import top.wetabq.wac.config.module.DefaultConfig

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class WSubCommand(val subCommandName: String) {

    abstract fun getArguments(): Array<CommandArgument>?

    abstract fun getAliases(): Array<String>

    abstract fun getDescription() : String

    abstract fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean

}