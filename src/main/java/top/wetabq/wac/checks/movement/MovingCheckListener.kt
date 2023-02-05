package top.wetabq.wac.checks.movement

import cn.nukkit.Player
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.player.*
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.item.Item
import cn.nukkit.network.protocol.MovePlayerPacket
import cn.nukkit.network.protocol.PlayerActionPacket
import cn.nukkit.network.protocol.PlayerAuthInputPacket
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.movement.checks.HighJump
import top.wetabq.wac.checks.movement.checks.NoFall
import top.wetabq.wac.checks.movement.checks.Speed
import top.wetabq.wac.checks.movement.checks.ThroughWall
import top.wetabq.wac.checks.utils.NoCheckUtils
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.module.DefaultModuleName

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class MovingCheckListener : Listener {

    @EventHandler
    fun onIM(event: PlayerInvalidMoveEvent) {
        event.setCancelled()
    }

    @EventHandler
    fun onTP(event: PlayerTeleportEvent) {
        val player = event.player
        NoCheckUtils.setNoCheck(CheckType.MOVING_SPEED, player, 20 * 5)
        NoCheckUtils.setNoCheck(CheckType.MOVING_NOFALL, player, 20 * 5)
        NoCheckUtils.setAllClear(player, event.to)
        val highJump = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.HIGHJUMP) as HighJump?)
        (highJump?.getPlayerCheckData(event.player) as MovingCheckData?)?.setJumpNoCheck(20 * 5)
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        if (player is Player) {
            NoCheckUtils.setNoCheck(CheckType.MOVING_SPEED, player, 20 * 5)
            NoCheckUtils.setNoCheck(CheckType.MOVING_NOFALL, player, 20 * 5)
            NoCheckUtils.setAllClear(player)
        }
    }

    @EventHandler
    fun onPlayerDie(event: PlayerDeathEvent) {
        val player = event.entity
        if (player is Player) {
            NoCheckUtils.setNoCheck(CheckType.MOVING_SPEED, player, 20 * 5)
            NoCheckUtils.setNoCheck(CheckType.MOVING_NOFALL, player, 20 * 5)
            NoCheckUtils.setAllClear(player)
        }
    }

    @EventHandler
    fun onDataPacketReceiveEvent(event: DataPacketReceiveEvent) {
        if (event.player.loggedIn) {
            val packet = event.packet
            val player = event.player
            val noFall = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.NOFALL) as NoFall?)
            noFall?.checkCheat(event.player, noFall.getPlayerCheckData(event.player), event)
            val speed = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.SPEED) as Speed?)
            speed?.checkCheat(event.player, speed.getPlayerCheckData(event.player), event)
            val id = player.level.getBlockIdAt(player.x.toInt(), player.y.toInt() - 1, player.z.toInt())
            if ((id == Item.ICE || id == 207 || id == Item.PACKED_ICE) || (player.level.getBlockIdAt(
                    player.x.toInt(), player.y.toInt() + 2, player.z.toInt()
                ) != 0)
            ) {
                (noFall?.getPlayerCheckData(player) as MovingCheckData?)?.setNoCheck(10 + player.ping)
                (speed?.getPlayerCheckData(player) as MovingCheckData?)?.setNoCheck(10 + player.ping)
            }
            //结局新版本nk问题
            if (packet is PlayerAuthInputPacket) {
                (noFall?.getPlayerCheckData(player) as MovingCheckData?)?.movePacketTracker?.addT(packet)
                (speed?.getPlayerCheckData(player) as MovingCheckData?)?.movePacketTracker?.addT(packet)
            }
            if (packet is PlayerActionPacket) {
                val size =
                    WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_TRACKERSIZE].toString().toInt()
                val checkData = (speed?.getPlayerCheckData(player) as MovingCheckData?)
                when (packet.action) {
                    PlayerActionPacket.ACTION_START_SNEAK -> {
                        if (checkData?.motionState != MovingCheckData.MotionState.SNEAKING) {
                            checkData?.motionState = MovingCheckData.MotionState.SNEAKING
                            checkData?.setNoCheck(size + 5)
                        }
                    }
                    PlayerActionPacket.ACTION_STOP_SNEAK -> {
                        if (checkData?.motionState != MovingCheckData.MotionState.WALK) {
                            checkData?.motionState = MovingCheckData.MotionState.WALK
                            checkData?.setNoCheck(size + 5)
                        }
                    }
                    PlayerActionPacket.ACTION_START_SPRINT -> {
                        if (checkData?.motionState != MovingCheckData.MotionState.SPRINT) {
                            checkData?.motionState = MovingCheckData.MotionState.SPRINT
                            checkData?.setNoCheck(size + 5)
                        }
                    }
                    PlayerActionPacket.ACTION_STOP_SPRINT -> {
                        if (checkData?.motionState != MovingCheckData.MotionState.WALK) {
                            checkData?.motionState = MovingCheckData.MotionState.WALK
                            checkData?.setNoCheck(size + 5)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlaceBlock(event: BlockPlaceEvent) {
        NoCheckUtils.setNoCheck(CheckType.MOVING_THROUGHWALL, event.player, 20 * 3)
        NoCheckUtils.setNoCheck(CheckType.MOVING_HIGHJUMP, event.player, 20 * 3)
    }

    @EventHandler
    fun onAttack(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val highJump = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.HIGHJUMP) as HighJump?)
            (highJump?.getPlayerCheckData(event.entity as Player) as MovingCheckData?)?.lastBeAttacked =
                System.currentTimeMillis()
            if (event.cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                NoCheckUtils.setNoJumpCheck((event.entity as Player), 20 * 5)
                NoCheckUtils.setNoCheck(CheckType.MOVING_SPEED, (event.entity as Player), 20 * 5)
            }
        }
    }

    @EventHandler
    fun onMove(event: DataPacketReceiveEvent) {
        if (!event.isCancelled) {
            if (event.packet is PlayerAuthInputPacket) {
                val highJump = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.HIGHJUMP) as HighJump?)
                highJump?.checkCheat(event.player, highJump.getPlayerCheckData(event.player), event)
                //PM1E核心额外
                val throughWall =
                    (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.THROUGHWALL) as ThroughWall?)
                throughWall?.checkCheat(event.player, throughWall.getPlayerCheckData(event.player), event)
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        Check.playerCheckDataMap[event.player.name]?.clear()
    }

    @EventHandler
    fun onPlayerSleep(event: PlayerBedLeaveEvent) {
        NoCheckUtils.setAllClearAndNoCheck(event.player, 20)
    }


}