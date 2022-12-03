package top.wetabq.wac.checks.chat.checks

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerChatEvent
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.chat.ChatCheckData
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.command.CommandArgument
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class ColorChat : Check<ChatCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_CHAT_COLORCHAT_REPLACE, true)
                    .addConfigPath(ConfigPaths.CHECKS_CHAT_COLORCHAT_ALLOWPLAYERS, arrayListOf<String>())
                    .addConfigPath(ConfigPaths.CHECKS_CHAT_COLORCHAT_MSG,"{WACTitle} &6&lYou are not allowed to use '§' characters")
                    .addCommand(object: WSubCommand("addAllowColorPlayer") {
                        override fun getAliases(): Array<String> {
                            return arrayOf("addAllowCP","addACP","addAllowColorPlayer")
                        }

                        override fun getArguments(): Array<CommandArgument> {
                            return arrayOf(CommandArgument("playerName","rawtext",false))
                        }

                        override fun getDescription(): String {
                            return "Add players who are allowed to use colored characters"
                        }

                        override fun execute(sender: CommandSender, label: String, args: Array<out String>,df: DefaultConfig): Boolean {
                            if(sender.isOp) {
                                val list = df.defaultConfig[ConfigPaths.CHECKS_CHAT_COLORCHAT_ALLOWPLAYERS] as ArrayList<String>?
                                list?.add(args[1])
                                sender.sendMessage(WAntiCheatPro.TITLE + "Successfully added players [${args[1]}]")
                            }
                            return true
                        }
                    })
                    .addCommand(object: WSubCommand("removeAllowColorPlayer") {
                        override fun getAliases(): Array<String> {
                            return arrayOf("removeAllowCP","removeACP","removeAllowColorPlayer")
                        }

                        override fun getArguments(): Array<CommandArgument> {
                            return arrayOf(CommandArgument("playerName","rawtext",false))
                        }

                        override fun getDescription(): String {
                            return "Remove players who are allowed to use colored characters"
                        }

                        override fun execute(sender: CommandSender, label: String, args: Array<out String>,df: DefaultConfig): Boolean {
                            if(sender.isOp) {
                                val list = df.defaultConfig[ConfigPaths.CHECKS_CHAT_COLORCHAT_ALLOWPLAYERS] as ArrayList<String>?
                                list?.remove(args[1])
                                sender.sendMessage(WAntiCheatPro.TITLE + "Successfully removed players [${args[1]}]")
                            }
                            return true
                        }
                    })
                    .registerCommands()
                    .setModuleName(DefaultModuleName.COLORCHAT)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.CHAT_COLOR
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is PlayerChatEvent && checkData is ChatCheckData) {
            if (event.message.contains("§")) {
                val allowList = df.defaultConfig[ConfigPaths.CHECKS_CHAT_COLORCHAT_ALLOWPLAYERS] as ArrayList<String>?
                if ((allowList?: arrayListOf()).contains(player.name)) return true
                if (df.defaultConfig[ConfigPaths.CHECKS_CHAT_COLORCHAT_REPLACE].toString().toBoolean()) {
                    event.message = event.message.replace("§","")
                } else {
                    event.player.sendMessage(WAntiCheatPro.translateMessage(df.defaultConfig[ConfigPaths.CHECKS_CHAT_COLORCHAT_MSG].toString()))
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.lowercase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                }
            }

        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}