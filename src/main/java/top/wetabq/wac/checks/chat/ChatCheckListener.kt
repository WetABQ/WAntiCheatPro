package top.wetabq.wac.checks.chat

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerChatEvent
import cn.nukkit.event.player.PlayerCommandPreprocessEvent
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.chat.checks.ColorChat
import top.wetabq.wac.checks.chat.checks.FastChat
import top.wetabq.wac.checks.chat.checks.FastCommand
import top.wetabq.wac.module.DefaultModuleName

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class ChatCheckListener : Listener {

    @EventHandler
    fun onPlayerChat(event: PlayerChatEvent) {
        val fastChat = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.FASTCHAT) as FastChat?)
        fastChat?.checkCheat(event.player,fastChat.getPlayerCheckData(event.player),event)
        val colorChat = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.COLORCHAT) as ColorChat?)
        colorChat?.checkCheat(event.player,colorChat.getPlayerCheckData(event.player),event)
    }

    @EventHandler
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val fastCommand = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.FASTCOMMAND) as FastCommand?)
        fastCommand?.checkCheat(event.player,fastCommand.getPlayerCheckData(event.player),event)
    }


}