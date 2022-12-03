package top.wetabq.wac.checks.event

import cn.nukkit.Player
import cn.nukkit.event.Cancellable
import cn.nukkit.event.HandlerList
import cn.nukkit.event.player.PlayerEvent
import top.wetabq.wac.checks.access.ViolationData

/**
 * easecation-root
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class PlayerViolationEvent(player : Player,private val violationData: ViolationData) : PlayerEvent(),Cancellable {

    init {
        super.player = player
    }

    fun getViolationData() : ViolationData{
        return violationData
    }

    companion object {

        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlers(): HandlerList {
            return handlers
        }

    }

}