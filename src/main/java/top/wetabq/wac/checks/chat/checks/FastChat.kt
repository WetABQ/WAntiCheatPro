package top.wetabq.wac.checks.chat.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerChatEvent
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.chat.ChatCheckData
import top.wetabq.wac.checks.exception.CheckCheatException
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
class FastChat : Check<ChatCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_CHAT_FASTCHAT_DELAY, 800)
                    .addConfigPath(ConfigPaths.CHECKS_CHAT_FASTCHAT_MSG,"{WACTitle} &6&lPlease don't speak so quickly")
                    .setModuleName(DefaultModuleName.FASTCHAT)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.CHAT_TEXT
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is PlayerChatEvent && checkData is ChatCheckData) {
            val delayLimit = df.defaultConfig[ConfigPaths.CHECKS_CHAT_FASTCHAT_DELAY].toString().toLong()
            val delay = System.currentTimeMillis() - checkData.lastChat
            if (delay < delayLimit) {
                val diff = (delayLimit - delay).toDouble() / 1000.toDouble()
                checkData.fastChatVL += diff
                checkData.playerCheat(checkData.fastChatVL,diff,"WAC Check #6")
                player.sendMessage(WAntiCheatPro.translateMessage(df.defaultConfig[ConfigPaths.CHECKS_CHAT_FASTCHAT_MSG].toString()))
                if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.lowercase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
            } else {
                checkData.fastChatVL *= 0.9
            }
            checkData.lastChat = System.currentTimeMillis()
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}