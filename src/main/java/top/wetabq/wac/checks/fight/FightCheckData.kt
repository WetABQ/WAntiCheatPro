package top.wetabq.wac.checks.fight

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
class FightCheckData(p : Player, check: Check<FightCheckData>, df : DefaultConfig) : CheckData(p,check,df){

    var autoAimVL = 0.0
    var criticalVL = 0.0
    var fightReachVL = 0.0
    var reachMod = 0.0
    var wrongAttackVL = 0.0
    var startJump = false
    var jumpIgTime = System.currentTimeMillis()

    override fun setBack() {

    }

    fun setJumpIgTime(tick: Int) {
        this.jumpIgTime = System.currentTimeMillis() + tick * 50
    }

    fun jumpIg() : Boolean {
        return jumpIgTime < System.currentTimeMillis()
    }

}