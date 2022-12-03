package top.wetabq.wac.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.utils.TextFormat
import top.wetabq.wac.WAntiCheatPro
import java.lang.StringBuilder
import java.util.HashMap

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class WCommand : Command("wac") {
    companion object {
        val subCommand = ArrayList<WSubCommand>()
    }

    init {
        this.setDescription("WAntiCheat Command")
        this.setCommandParameters(object : HashMap<String, Array<CommandParameter>>() {
            init {
                var i = 1
                for (sc in subCommand) {
                    val strs = StringBuilder()
                    strs.let{ sc.getAliases().forEach { aliases -> it.append("$aliases/")  }}
                    if (sc.getArguments() != null) {
                        val commandParameters = arrayOf(CommandParameter("${sc.subCommandName}($strs)", false, sc.getAliases()))
                        sc.getArguments()?.forEach { argSetting -> commandParameters.plus(CommandParameter(argSetting.argName,argSetting.argType,argSetting.optional)) }
                        put("${i}arg", commandParameters)
                    } else put("${i}arg", arrayOf(CommandParameter("${sc.subCommandName}(${strs})", false, sc.getAliases())))
                    i++
                }
            }
        })
        this.usage = "/wac <subcommand> [args]"
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }
        for (sc in subCommand) {
            run{
                if (args[0].equals(sc.subCommandName)) {
                    return sc.execute(sender, label, args, WAntiCheatPro.df)
                }
                for (aliases in sc.getAliases()) {
                    if (args[0].equals(aliases)) return sc.execute(sender, label, args,WAntiCheatPro.df)
                }
            }
        }
        sendHelp(sender)
        return true
    }

    fun sendHelp(sender: CommandSender) {
        sender.sendMessage(TextFormat.GOLD.toString() + "----WAntiCheatPro----")
        for (sc in subCommand) {
            val strs = StringBuilder()
            strs.let{
                for (aliases in sc.getAliases()) {
                    it.append("$aliases/")
                }}
            sender.sendMessage("${TextFormat.AQUA}/wac ${sc.subCommandName}($strs) - ${sc.getDescription()}")
        }
    }

}