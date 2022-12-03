package top.wetabq.wac.checks.movement.checks

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.event.Event
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.math.Vector3
import cn.nukkit.network.protocol.MovePlayerPacket
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.fight.FightCheckData
import top.wetabq.wac.checks.fight.checks.Critical
import top.wetabq.wac.checks.movement.MovingCheckData
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class NoFall : Check<MovingCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_MOVING_NOFALL_YLIMIT,3.5)
                    .setModuleName(DefaultModuleName.NOFALL)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.MOVING_NOFALL
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (player.isCreative || player.isSpectator) return true
        if (checkData is MovingCheckData && event is DataPacketReceiveEvent) {
            val packet = event.packet
            if (packet is MovePlayerPacket && !player.isSwimming && !player.isSleeping && player.riding == null && (player.isSurvival || player.isAdventure) && checkData.movePacketTracker.isFull() && packet.mode != MovePlayerPacket.MODE_TELEPORT) {
                if (player.level.getBlockIdAt(player.x.toInt(), player.y.toInt() - 1, player.z.toInt()) == Block.SLIME_BLOCK || player.level.getBlockIdAt(player.x.toInt(), player.y.toInt() - 2, player.z.toInt()) == Block.SLIME_BLOCK || player.level.getBlockIdAt(player.x.toInt(), player.y.toInt() - 3, player.z.toInt()) == Block.SLIME_BLOCK) checkData.setNoCheck(20*3)
                val yLimit = df.defaultConfig[ConfigPaths.CHECKS_MOVING_NOFALL_YLIMIT].toString().toDouble()
                val critical = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.CRITICAL) as Critical?)
                val fightCheckData = (critical?.getPlayerCheckData(player) as FightCheckData?)
                //checkDebug(player,"NF+: DEBUG FallY=${packet.y - checkData.movePacketTracker.getLast().y}")
                if (!(fightCheckData?.startJump?: false) && packet.y - checkData.movePacketTracker.getLast().y > yLimit) {
                    val diff = yLimit - (checkData.movePacketTracker.getLast().y - packet.y)
                    if (checkData.playerCheat(checkData.noFallVL,diff,"WAC Check #12")) checkData.noFallVL += diff
                    checkDebug(player,"NF+: CHECKED vl=${yLimit - (checkData.movePacketTracker.getLast().y - packet.y)} totalVl=${checkData.noFallVL}")
                } //没有衰减
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}