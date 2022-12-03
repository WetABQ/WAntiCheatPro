package top.wetabq.wac.checks.event

import cn.nukkit.Player
import cn.nukkit.event.Cancellable
import cn.nukkit.event.HandlerList
import cn.nukkit.event.player.PlayerEvent
import top.wetabq.wac.checks.CheckType

/**
 * WAntiCheat
 *
 * @author WetABQ Copyright (c) 2018.07
 * @version 1.0
 */
class CheckPlayerCheatEvent(player : Player,private val cheatType : CheckType) : PlayerEvent(),Cancellable {

    init {
        super.player = player
    }


    fun getCheatType(): CheckType {
        return cheatType
    }

    companion object {

        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlers(): HandlerList {
            return handlers
        }

    }

}