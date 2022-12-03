package top.wetabq.wac.checks.fight.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.item.Item
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.fight.FightCheckData
import top.wetabq.wac.checks.utils.BlockUtils
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
class WrongAttack : Check<FightCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_FIGHT_WRONGATTACK_BLOCKS,3)
                    .setModuleName(DefaultModuleName.WRONGATTACK)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.FIGHT_WRONGATTACK
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is EntityDamageByEntityEvent && checkData is FightCheckData) {
            if (player.inventory.itemInHand.id != Item.BOW && event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && !(event is EntityDamageByChildEntityEvent)) {
                val blockLimit = df.defaultConfig[ConfigPaths.CHECKS_FIGHT_WRONGATTACK_BLOCKS].toString().toInt()
                val loc = player.location
                loc.y = loc.getY() + player.eyeHeight
                val blockList = BlockUtils.getLineBlock(loc, event.entity)
                if (blockList.size >= blockLimit) {
                    // They failed, increment violation level.
                    val diff = (blockList.size - blockLimit).toDouble()
                    checkData.wrongAttackVL += diff
                    checkData.playerCheat(checkData.wrongAttackVL, diff,"WAC Check #10")

                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                } else {
                    // Player passed the check, reward them.
                    checkData.wrongAttackVL *= 0.9
                }
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}