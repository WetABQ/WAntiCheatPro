package top.wetabq.wac.checks.movement

import cn.nukkit.Player
import cn.nukkit.level.Location
import cn.nukkit.math.Vector3
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.movement.tracker.MovePacketTracker
import top.wetabq.wac.checks.movement.tracker.PositionTracker
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class MovingCheckData(p: Player, check: Check<MovingCheckData>, df: DefaultConfig) : CheckData(p, check, df) {

    var highJumpVL = 0.0
    var flyVL = 0.0
    var noFallVL = 0.0
    var instantVL = 0.0
    var speedVL = 0.0
    var throughWallVL = 0.0

    var lowestY = 0.0
    var fall4block: Location? = null
    var lastOnGround: Location? = null
    var lastHighestY = 0.0
    var lastJumpLocation = p.location
    var lastBeAttacked = System.currentTimeMillis()
    var noJumpCheck = System.currentTimeMillis()
    var motionState = MotionState.WALK

    val movePacketTracker = MovePacketTracker(p, df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_TRACKERSIZE].toString().toInt(), this)
    val positionTracker = PositionTracker(p, df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_TRACKERSIZE].toString().toInt())

    fun isAttcked(): Boolean {
        return System.currentTimeMillis() - lastBeAttacked < df.defaultConfig[ConfigPaths.CHECKS_MOVING_HIGHJUMP_ATTACKTIME].toString().toLong()
    }

    fun setJumpNoCheck(tick: Int) {
        if (this.noJumpCheck > System.currentTimeMillis() + tick * 50 && tick != 0) return
        this.noJumpCheck = System.currentTimeMillis() + tick * 50
    }

    fun doJumpCheck(): Boolean {
        return noJumpCheck < System.currentTimeMillis()
    }

    override fun setBack() {

    }

    enum class MotionState {
        SPRINT,
        WALK,
        SNEAKING
    }
}