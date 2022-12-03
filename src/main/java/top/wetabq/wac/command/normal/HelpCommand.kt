package top.wetabq.wac.command.normal

import cn.nukkit.command.CommandSender
import top.wetabq.wac.command.CommandArgument
import top.wetabq.wac.command.WACommand
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.command.reg.RegCommandModule
import top.wetabq.wac.config.module.DefaultConfig

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class HelpCommand : WACommand() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegCommandModule) {
            registry.addCommand(object : WSubCommand("help"){
                override fun getAliases(): Array<String> {
                    return arrayOf("h","help")
                }

                override fun getArguments(): Array<CommandArgument>? {
                    return null
                }

                override fun execute(sender: CommandSender, label: String, args: Array<out String>,df: DefaultConfig): Boolean {
                    WACommand.wCommand?.sendHelp(sender)
                    return true
                }

                override fun getDescription(): String {
                    return "View command help"
                }
            })
                    .registerCommands()
                    .setModuleName("HelpCommand")
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

}