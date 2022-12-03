package top.wetabq.wac.checks.movement.checks

import cn.nukkit.AdventureSettings
import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerMoveEvent
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.exception.CheckCheatException
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
class ThroughWall : Check<MovingCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.setModuleName(DefaultModuleName.THROUGHWALL)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.MOVING_THROUGHWALL
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (checkData is MovingCheckData && event is PlayerMoveEvent) {
            if (player.gamemode == 0 && !player.adventureSettings[AdventureSettings.Type.NO_CLIP]) {
                val radius = player.width.toDouble() / 2.0
                val bb = player.getBoundingBox().clone().setBounds(
                        player.x - radius + 0.25, player.y + 1.5, player.z - radius + 0.25,
                        player.x + radius - 0.25, player.y + (player.height * player.scale) - 0.1, player.z + radius - 0.25
                )
                for (block in player.getBlocksAround()) {
                    if (block.collidesWithBB(bb) && block.isSolid && block.isNormalBlock && !block.canPassThrough()) {
                        checkData.throughWallVL += 1.0
                        checkData.setNoCheck(20)
                        checkDebug(player, "TW+: CHECKED vl=1 totalVl=${checkData.throughWallVL}")
                        checkData.playerCheat(checkData.throughWallVL, 1.0, "#WAC Check #19")
                        if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                    }
                }
            }

        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}