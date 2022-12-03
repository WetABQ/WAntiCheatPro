package top.wetabq.wac.checks.block.blockbreak

import cn.nukkit.Player
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.block.blockbreak.checks.ReachBreak
import top.wetabq.wac.config.module.DefaultConfig

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class BlockBreakData(p : Player,check: Check<BlockBreakData>,df : DefaultConfig) : CheckData(p,check,df) {

    var fastBreakVL = 0.0
    var breakReachVL = 0.0
    var wrongBreakVL = 0.0
    var lastBreak = System.currentTimeMillis()
    var reachDistance = ReachBreak.SURVIVAL_DISTANCE
    var lastBlocks = 0

    override fun setBack() {
        lastBreak = System.currentTimeMillis()
        fastBreakVL = 0.0
        breakReachVL = 0.0
        wrongBreakVL = 0.0
        reachDistance = ReachBreak.SURVIVAL_DISTANCE
        lastBlocks = 0
    }

}