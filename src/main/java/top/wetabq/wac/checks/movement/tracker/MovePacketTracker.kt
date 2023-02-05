package top.wetabq.wac.checks.movement.tracker

import cn.nukkit.Player
import cn.nukkit.math.Vector3
import cn.nukkit.network.protocol.PlayerAuthInputPacket
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.CheckTracker
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.movement.MovingCheckData
import top.wetabq.wac.checks.movement.checks.Speed
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.module.DefaultModuleName
import java.util.*
import kotlin.math.pow

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class MovePacketTracker(player: Player, size: Int, private val checkData: MovingCheckData) :
    CheckTracker<PlayerAuthInputPacket>(player, size) { // 连续记录 | MovePacket不受到延迟干扰

    private var checkPacketCountTime = System.currentTimeMillis()
    private var sendMovePacketCount = 0
    var packetCountQueue = LinkedList<Int>()
    var sendVariance = 0.0
    override fun addT(t: PlayerAuthInputPacket) {
        super.addT(t)
        if (checkPacketCountTime < System.currentTimeMillis()) {
            this.checkPacketCountTime = System.currentTimeMillis() + 1000 // 1s
            if (this.packetCountQueue.size >= size) {
                var sum = 0.0
                var up = 0.0
                for (count in this.packetCountQueue) {
                    sum += count
                }
                val avg = sum / size.toDouble()
                for (count in this.packetCountQueue) {
                    up += Math.pow(count - avg, 2.0)
                }
                sendVariance = up / size.toDouble()
                this.packetCountQueue.poll()
                val packetLimit =
                    WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXMOVEPACKET].toString().toInt()
                //println("DEBUG packetCountQueue=$packetCountQueue avg=$avg sendV=$sendVariance")
                if (avg > packetLimit && sum > packetLimit * (size) * 2 && sendVariance <= 200 && checkData.doCheck(
                        CheckType.MOVING_SPEED
                    )
                ) {
                    checkData.speedVL += avg - packetLimit
                    val speed = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.SPEED) as Speed?)
                    speed?.checkDebug(player, "SD+: CHECKED 7 vl=${avg - packetLimit} totalVl=${checkData.speedVL}")
                    checkData.playerCheat(checkData.speedVL, avg - packetLimit, "WAC Check #23")
                }
            }
            this.packetCountQueue.offer(this.sendMovePacketCount)
            this.sendMovePacketCount = 0
        } else {
            this.sendMovePacketCount++
        }
    }

    fun getInstant(v: PlayerAuthInputPacket): Double {
        val lastPacket = getLast()
        return Vector3(lastPacket.position.x.toDouble(), 0.toDouble(), lastPacket.position.z.toDouble()).distance(
            Vector3(
                v.position.x.toDouble(),
                0.toDouble(),
                v.position.z.toDouble()
            )
        )
    }

    override fun getAverage(): Double {
        var sumDistance = 0.0
        var lastPacket: PlayerAuthInputPacket? = null
        for (v in getTrackerQueue()) {
            if (lastPacket is PlayerAuthInputPacket) {
                sumDistance += Vector3(lastPacket.position.x.toDouble(), 0.toDouble(), lastPacket.position.z.toDouble()).distance(
                    Vector3(
                        v.position.x.toDouble(),
                        0.toDouble(),
                        v.position.z.toDouble()
                    )
                )
            }
            lastPacket = v
        }
        return sumDistance / size.toDouble()
    }

    override fun getVariance(): Double {
        var sumV = 0.0
        val avg = getAverage()
        var lastPacket: PlayerAuthInputPacket? = null
        for (v in getTrackerQueue()) {
            if (lastPacket is PlayerAuthInputPacket) {
                val speed = Vector3(
                    lastPacket.position.x.toDouble(),
                    0.toDouble(),
                    lastPacket.position.z.toDouble()
                ).distance(Vector3(v.position.x.toDouble(), 0.toDouble(), v.position.z.toDouble()))
                sumV += (speed - avg).pow(2.toDouble())
            }
            lastPacket = v
        }
        return sumV / size.toDouble()
    }

}