package top.wetabq.wac.checks.block.blockbreak.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockBreakEvent
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.block.blockbreak.BlockBreakData
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class ReachBreak : Check<BlockBreakData>() {

    companion object {
        /** The maximum distance allowed to interact with a block in creative mode.  */
        val CREATIVE_DISTANCE = 5.8

        /** The maximum distance allowed to interact with a block in survival mode.  */
        val SURVIVAL_DISTANCE = 5.4
    }

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_BREAK_REACH_CREATIVE_DISTANCE,CREATIVE_DISTANCE)
                    .addConfigPath(ConfigPaths.CHECKS_BREAK_REACH_SURVIVAL_DISTANCE, SURVIVAL_DISTANCE)
                    .setModuleName(DefaultModuleName.REACHBREAK)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.BLOCKBREAK_REACH
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig): Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is BlockBreakEvent && checkData is BlockBreakData) {
            val distanceLimit = if (player.gamemode == Player.CREATIVE) df.defaultConfig[ConfigPaths.CHECKS_BREAK_REACH_CREATIVE_DISTANCE].toString().toDouble() else df.defaultConfig[ConfigPaths.CHECKS_BREAK_REACH_SURVIVAL_DISTANCE].toString().toDouble()
            // Distance is calculated from eye location to center of targeted block. If the player is further away from their
            // target than allowed, the difference will be assigned to "distance".
            val loc = player.location
            loc.y = loc.getY() + player.eyeHeight
            val distance = loc.distance(event.block) - distanceLimit

            if (distance > 0) {
                // They failed, increment violation level.
                if(checkData.playerCheat(checkData.breakReachVL,distance,"WAC + NCP Check #2")) checkData.breakReachVL += distance
                checkDebug(player,"RB+: distance=$distance totalVL=${checkData.breakReachVL}")

                // Remember how much further than allowed he tried to reach for logging, if necessary.
                checkData.reachDistance = distance
                if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.lowercase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
            } else {
                // Player passed the check, reward them.
                checkData.breakReachVL *= 0.9
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true

    }

}