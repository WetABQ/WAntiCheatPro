package top.wetabq.wac.checks.block.blockplace

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockPlaceEvent
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.block.blockplace.checks.PlaceReach
import top.wetabq.wac.checks.block.blockplace.checks.WrongPlace
import top.wetabq.wac.module.DefaultModuleName

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class BlockPlaceListener : Listener {

    @EventHandler
    fun onPlayerBlockPlaceEvent(event: BlockPlaceEvent) {
        val placeReach = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.PLACEREACH) as PlaceReach?)
        placeReach?.checkCheat(event.player,placeReach.getPlayerCheckData(event.player),event)
        val wrongPlace = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.WRONGPLACE) as WrongPlace?)
        wrongPlace?.checkCheat(event.player,wrongPlace.getPlayerCheckData(event.player),event)
    }

}