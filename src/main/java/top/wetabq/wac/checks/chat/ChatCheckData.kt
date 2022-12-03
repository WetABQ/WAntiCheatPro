package top.wetabq.wac.checks.chat

import cn.nukkit.Player
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.config.module.DefaultConfig

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class ChatCheckData(p : Player, check: Check<ChatCheckData>, df : DefaultConfig) : CheckData(p,check,df) {

    var lastChat = -1L
    var lastCommand = -1L
    var fastChatVL = 0.0
    var fastCommandVL = 0.0

    override fun setBack() {
        lastChat = System.currentTimeMillis()
        lastCommand = System.currentTimeMillis()
        fastChatVL = 0.0
        fastCommandVL = 0.0
    }

}