package top.wetabq.wac.checks.block.blockplace.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockPlaceEvent
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.block.blockplace.BlockPlaceData
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
class PlaceReach : Check<BlockPlaceData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.setModuleName(DefaultModuleName.PLACEREACH)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.BLOCKPLACE_REACH
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is BlockPlaceEvent && checkData is BlockPlaceData) {
            val distanceLimit = if (player.gamemode == Player.CREATIVE) df.defaultConfig[ConfigPaths.CHECKS_BREAK_REACH_CREATIVE_DISTANCE].toString().toDouble() else df.defaultConfig[ConfigPaths.CHECKS_BREAK_REACH_SURVIVAL_DISTANCE].toString().toDouble()
            val loc = player.location
            loc.y = loc.getY() + player.eyeHeight
            val distance = loc.distance(event.block) - distanceLimit

            if (distance > 0) {
                // They failed, increment violation level.
                checkData.placeReachVL += distance
                checkData.playerCheat(checkData.placeReachVL,distance,"WAC + NCP Check #4")

                // Remember how much further than allowed he tried to reach for logging, if necessary.
                checkData.reachDistance = distance

            } else {
                // Player passed the check, reward them.
                checkData.placeReachVL *= 0.9
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}