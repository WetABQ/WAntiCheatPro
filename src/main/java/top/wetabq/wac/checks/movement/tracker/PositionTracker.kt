package top.wetabq.wac.checks.movement.tracker

import cn.nukkit.Player
import cn.nukkit.level.Position
import top.wetabq.wac.checks.CheckTracker

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class PositionTracker(player: Player, size: Int) : CheckTracker<Position>(player,size) { //相同位置不记录 | 非连续记录 | 抗延迟

    override fun addT(t: Position) {
        val lastPos = getLast()
        if (lastPos != null) {
            if (lastPos.x == t.x && lastPos.y == t.y && lastPos.z == t.z) return
        }
        super.addT(t)
    }

    override fun getAverage(): Double {
        var sumDistance = 0.0
        var lastPos : Position? = null
        for (v in getTrackerQueue()) {
            val a = v.subtract(0.toDouble(),v.y)
            if (lastPos is Position) {
                sumDistance += lastPos.distance(a)
            }
            lastPos = a
        }
        return sumDistance / size.toDouble()
    }

    override fun getVariance(): Double {
        var sumV = 0.0
        val avg = getAverage()
        var lastPos : Position? = null
        for (v in getTrackerQueue()) {
            val a = v.subtract(0.toDouble(),v.y)
            if (lastPos is Position) {
                val speed = lastPos.distance(a)
                sumV += Math.pow(speed - avg,2.toDouble())
            }
            lastPos = a
        }
        return sumV / size.toDouble()
    }


}