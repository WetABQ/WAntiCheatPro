package top.wetabq.wac.checks.utils

import cn.nukkit.Player
import cn.nukkit.level.Location
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.fight.FightCheckData
import top.wetabq.wac.checks.movement.MovingCheckData

/**
 * easecation-root
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
object NoCheckUtils {
    var noCheckAll = HashMap<String,Long>()

    @JvmStatic
    fun setAllNoCheck(player: Player, tick: Int) {
        var flag = true
        noCheckAll[player.name]?.let {
            if (it > System.currentTimeMillis() + tick * 50 && tick != 0) flag = false
        }
        if (flag) noCheckAll[player.name] = System.currentTimeMillis() + tick * 50
    }

    @JvmStatic
    fun setAllClearAndNoCheck(player: Player, tick: Int) {
        setAllNoCheck(player,tick)
        setAllClear(player,player.location)
    }

    @JvmStatic
    fun setAllClearAndNoCheck(player: Player, tick: Int, to: Location) {
        setAllNoCheck(player,tick)
        setAllClear(player,to)
    }

    @JvmStatic
    fun setAllClear(player: Player, to: Location) {
        if (Check.playerCheckDataMap.containsKey(player.name)) {
            Check.playerCheckDataMap[player.name]?.values?.forEach { v ->
                if (v is MovingCheckData) {
                    v.lastJumpLocation = to
                    v.lastBeAttacked = System.currentTimeMillis()
                    v.motionState = MovingCheckData.MotionState.SPRINT
                    v.lastOnGround = to
                    v.lastJumpLocation = to
                    v.movePacketTracker.clearAll()
                    v.lowestY = to.y
                    v.lastHighestY = to.y
                    v.setJumpNoCheck(20*5)
                } else if(v is FightCheckData) {
                    v.setJumpIgTime(10)
                }
            }
        }
    }

    @JvmStatic
    fun setAllClear(player: Player) {
        setAllClear(player,player.location)
    }

    @JvmStatic
    fun setNoCheck(checkType: CheckType,player: Player,tick: Int) {
        Check.getPlayerCheatData(checkType,player)?.let {
            it.setNoCheck(tick)
        }
    }

    @JvmStatic
    fun setNoJumpCheck(player: Player,tick: Int) {
        Check.getPlayerCheatData(CheckType.MOVING_HIGHJUMP,player)?.let {
            if (it is MovingCheckData) {
                it.setJumpNoCheck(tick)
            }
        }
    }

    @JvmStatic
    fun setJumpIgTime(player: Player,tick: Int) {
        Check.getPlayerCheatData(CheckType.MOVING_HIGHJUMP,player)?.let {
            if (it is FightCheckData) {
                it.setJumpIgTime(tick)
            }
        }
    }

}