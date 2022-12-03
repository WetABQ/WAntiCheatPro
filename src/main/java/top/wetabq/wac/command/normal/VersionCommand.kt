package top.wetabq.wac.command.normal

import cn.nukkit.command.CommandSender
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.command.CommandArgument
import top.wetabq.wac.command.WACommand
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.command.reg.RegCommandContext
import top.wetabq.wac.command.reg.RegCommandModule
import top.wetabq.wac.config.module.DefaultConfig

/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class VersionCommand : WACommand() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegCommandModule) {
            registry.addCommand(object : WSubCommand("version"){
                override fun getAliases(): Array<String> {
                    return arrayOf("v","version")
                }

                override fun getArguments(): Array<CommandArgument>? {
                    return null
                }

                override fun execute(sender: CommandSender, label: String, args: Array<out String>,df: DefaultConfig): Boolean {
                    sender.sendMessage("${(context as RegCommandContext).commandPrefix}WAntiCheatPro - ${WAntiCheatPro.VERSION} by WetABQ\n" +
                            "${(context as RegCommandContext).commandPrefix}If you have any questions, please feel free to send us feedback to our email wetabq@gmail.com")
                    return true
                }

                override fun getDescription(): String {
                    return "View WAC's version"
                }
            })
                    .registerCommands()
                    .setModuleName("VersionCommand")
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

}