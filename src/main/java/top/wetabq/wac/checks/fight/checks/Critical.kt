package top.wetabq.wac.checks.fight.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.potion.Effect
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.fight.FightCheckData
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
class Critical : Check<FightCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_FIGHT_CRITICAL_FALLDISTANCE,0.06251)
                    .setModuleName(DefaultModuleName.CRITICAL)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.FIGHT_CRITICAL
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (checkData is FightCheckData) {
            val mcFallDistance = player.fallDistance
            // Check if the hit was a critical hit (very small fall-distance, not on ladder,
            //  not in liquid, not in vehicle, and without blindness effect).
            if (mcFallDistance > 0.0 && player.riding == null && !player.hasEffect(Effect.BLINDNESS)) {
                checkDebug(player,"y=" + player.getY() + " mcfalldist=" + mcFallDistance + " jump=${checkData.startJump}")
                // TODO: Skip near the highest jump height (needs check if head collided with something solid, which also detects low jump).
                if (!checkData.startJump || mcFallDistance < df.defaultConfig[ConfigPaths.CHECKS_FIGHT_CRITICAL_FALLDISTANCE].toString().toDouble()) {
                    checkData.criticalVL += 1.0
                    checkData.playerCheat(checkData.criticalVL,1.0,"WAC + NCP Check #9")
                    checkDebug(player, "AC+: Critical CHECKED vlTotal=${checkData.criticalVL} jump=${checkData.startJump}")
                } else {
                    checkData.criticalVL *= 0.99
                }
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}