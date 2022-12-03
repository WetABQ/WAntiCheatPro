package top.wetabq.wac.checks.block.blockbreak

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.PlayerActionPacket
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.block.blockbreak.checks.FastBreak
import top.wetabq.wac.checks.block.blockbreak.checks.ReachBreak
import top.wetabq.wac.checks.block.blockbreak.checks.WrongBreak
import top.wetabq.wac.module.DefaultModuleName

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class BlockBreakListener : Listener {

    @EventHandler
    fun onDataPacket(event: DataPacketReceiveEvent){
        val player = event.player
        if (player.loggedIn) {
            if (event.packet is PlayerActionPacket) {
                val actionPacket = event.packet as PlayerActionPacket
                if (actionPacket.action == PlayerActionPacket.ACTION_START_BREAK) {
                    val fastBreak = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.FASTBREAK) as FastBreak?)
                    (fastBreak?.getPlayerCheckData(event.player) as BlockBreakData?)?.lastBreak = System.currentTimeMillis()
                }
            }
        }
    }

    @EventHandler
    fun onPlayerBreakEvent(event: BlockBreakEvent) {
        val fastBreak = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.FASTBREAK) as FastBreak?)
        fastBreak?.checkCheat(event.player,fastBreak.getPlayerCheckData(event.player),event)
        val reachBreak = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.REACHBREAK) as ReachBreak?)
        reachBreak?.checkCheat(event.player,reachBreak.getPlayerCheckData(event.player),event)
        val wrongBreak = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.WRONGBREAK) as WrongBreak?)
        wrongBreak?.checkCheat(event.player,wrongBreak.getPlayerCheckData(event.player),event)
    }

}