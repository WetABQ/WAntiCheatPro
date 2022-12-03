package top.wetabq.wac.checks.utils

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.math.Vector3
import cn.nukkit.network.protocol.MovePlayerPacket




/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
object MathUtils {

    @JvmStatic
    /**
     * @param player 被瞄准玩家
     * @param entity 瞄准始发点
     */
    fun look(player: Entity, entity: Entity): Pair<Double,Double> {
        val yaw = this.getyaw(player.x - entity.x, player.z - entity.z)
        val pitch = this.getpitch(entity, player)
        return Pair(yaw,pitch)
    }

    fun look2(player: Entity,entity: Entity): Pair<Double,Double> {
        val x = entity.x - player.x
        val y = entity.y - player.y
        val z = entity.z - player.z
        var yaw = Math.asin(x / Math.sqrt(x * x + z * z)) / 3.14 * 180
        val pitch = Math.round(Math.asin(y / Math.sqrt(x * x + z * z + y * y)) / 3.14 * 180).toDouble()
        if (z > 0) {
            yaw = -yaw + 180
        }
       return Pair(yaw,pitch)
    }

    fun inRange(range: Double,dR: Double,v: Double): Boolean {
        val rangeMax = range + dR
        val rangeMin = range - dR
        return rangeMin < v && v < rangeMax
    }

    fun getyaw(mx: Double, mz: Double): Double {  //根据motion计算转向角度
        if (mz == 0.0) return (if (mx >= 0) 0 else 180).toDouble()
        val yaw = Math.toDegrees(-Math.atan(mx / mz))
        return if (mz > 0) yaw else yaw + 180
    }

    fun getpitch(from: Vector3, to: Vector3): Double {
        val distance = from.distance(to)
        return -Math.toDegrees(Math.asin((to.y - from.y) / distance))
    }

}