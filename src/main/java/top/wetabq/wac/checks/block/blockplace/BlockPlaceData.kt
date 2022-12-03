package top.wetabq.wac.checks.block.blockplace

import cn.nukkit.Player
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.block.blockbreak.checks.ReachBreak
import top.wetabq.wac.checks.block.blockplace.checks.PlaceReach
import top.wetabq.wac.config.module.DefaultConfig

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class BlockPlaceData(p : Player, check: Check<BlockPlaceData>, df : DefaultConfig) : CheckData(p,check,df) {

    var placeReachVL = 0.0
    var wrongPlaceVL = 0.0
    var reachDistance = ReachBreak.SURVIVAL_DISTANCE
    var lastBlocks = 0

    override fun setBack() {
        placeReachVL = 0.0
        wrongPlaceVL = 0.0
        reachDistance = ReachBreak.SURVIVAL_DISTANCE
        lastBlocks = 0
    }

}