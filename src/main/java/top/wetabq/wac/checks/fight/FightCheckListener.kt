package top.wetabq.wac.checks.fight

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.block.BlockLadder
import cn.nukkit.block.BlockVine
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.player.PlayerMoveEvent
import cn.nukkit.event.server.BatchPacketsEvent
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.AnimatePacket
import cn.nukkit.network.protocol.PlayerActionPacket
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.fight.checks.AutoAim
import top.wetabq.wac.checks.fight.checks.Critical
import top.wetabq.wac.checks.fight.checks.FightReach
import top.wetabq.wac.checks.fight.checks.WrongAttack
import top.wetabq.wac.module.DefaultModuleName

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class FightCheckListener : Listener {

    @EventHandler
    fun onDataPacket(event: DataPacketReceiveEvent){
        val player = event.player
        if (player.loggedIn) {
            if (event.packet is PlayerActionPacket) {
                val actionPacket = event.packet as PlayerActionPacket
                if (actionPacket.action == PlayerActionPacket.ACTION_JUMP) {
                    val critical = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.CRITICAL) as Critical?)
                    val fightCheckData = (critical?.getPlayerCheckData(event.player) as FightCheckData?)
                    fightCheckData?.startJump = true
                    fightCheckData?.setJumpIgTime(18)
                }
            }
        }
    }

    @EventHandler
    fun onPacket(event: BatchPacketsEvent) {
        val packets = event.packets
        for (pk in packets) {
            if(pk is AnimatePacket && pk.action == AnimatePacket.Action.CRITICAL_HIT) {
                for (p in event.players) {
                    if (p.id == pk.eid) {
                        val last = p.lastDamageCause
                        if (last is EntityDamageByEntityEvent && last.damager is Player) {
                            val critical = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.CRITICAL) as Critical?)
                            critical?.checkCheat(last.damager as Player, critical.getPlayerCheckData(last.damager as Player), event)
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    fun onDamage(event: EntityDamageEvent) {
        if (event is EntityDamageByEntityEvent) {
            if (event.damager is Player) {
                val autoAim = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.AUTOAIM) as AutoAim?)
                autoAim?.checkCheat(event.damager as Player, autoAim.getPlayerCheckData(event.damager as Player), event)
                val fightReach = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.FIGHTREACH) as FightReach?)
                fightReach?.checkCheat(event.damager as Player, fightReach.getPlayerCheckData(event.damager as Player), event)
                val wrongAttack = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.WRONGATTACK) as WrongAttack?)
                wrongAttack?.checkCheat(event.damager as Player, wrongAttack.getPlayerCheckData(event.damager as Player), event)
            }
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val critical = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.CRITICAL) as Critical?)
        val fightCheckData = (critical?.getPlayerCheckData(event.player) as FightCheckData?)
        fightCheckData?.let { _ ->
            if (fightCheckData.startJump && (event.player.onGround || (event.player.getBlocksAround().let {
                    var flag = false
                    it.forEach { block -> flag = block is BlockLadder || block is BlockVine }
                    flag
                } || (event.player.level.getBlock(event.player) is BlockLadder || event.player.level.getBlock(event.player) is BlockVine))) && fightCheckData.jumpIg()) {
                fightCheckData.startJump = false
            }
            if (fightCheckData.startJump && event.player.isInsideOfWater || event.player.level.getBlock(
                    event.player.position.floor().add(0.5, -1.0, 0.5)
                ).id == Block.SLIME_BLOCK && fightCheckData.jumpIg()
            ) {
                fightCheckData.startJump = false
            }
        }
    }

}