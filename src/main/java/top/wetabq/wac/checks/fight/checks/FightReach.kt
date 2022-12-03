package top.wetabq.wac.checks.fight.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.item.Item
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
class FightReach : Check<FightCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_FIGHT_REACH_DYNAMICCHECK,true)
                    .addConfigPath(ConfigPaths.CHECKS_FIGHT_REACH_DYNAMICRANGE,0.9)
                    .addConfigPath(ConfigPaths.CHECKS_FIGHT_REACH_DYNAMICRANGESTEP,0.15)
                    .addConfigPath(ConfigPaths.CHECKS_FIGHT_REACH_CREATIVE_DISTANCE, 5.3)
                    .addConfigPath(ConfigPaths.CHECKS_FIGHT_REACH_SURVIVAL_DISTANCE, 3.9)
                    .setModuleName(DefaultModuleName.FIGHTREACH)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.FIGHT_REACH
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is EntityDamageByEntityEvent && checkData is FightCheckData) {
            val damager = event.damager
            if (damager is Player && event.entity is Player && damager.inventory.itemInHand.id != Item.BOW && event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                // The maximum distance allowed to interact with an entity in survival mode.
                val SURVIVAL_DISTANCE = df.defaultConfig[ConfigPaths.CHECKS_FIGHT_REACH_SURVIVAL_DISTANCE].toString().toDouble() // 4.4D;
                val CREATIVE_DISTANCE = df.defaultConfig[ConfigPaths.CHECKS_FIGHT_REACH_CREATIVE_DISTANCE].toString().toDouble()
                // Amount which can be reduced by reach adaption.
                val DYNAMIC_RANGE = df.defaultConfig[ConfigPaths.CHECKS_FIGHT_REACH_DYNAMICRANGE].toString().toDouble() // 0.9
                // Adaption amount for dynamic range.
                val DYNAMIC_STEP = df.defaultConfig[ConfigPaths.CHECKS_FIGHT_REACH_DYNAMICRANGESTEP].toString().toDouble() / SURVIVAL_DISTANCE // 0.15

                val distanceLimit = if (player.gamemode == Player.CREATIVE) CREATIVE_DISTANCE else SURVIVAL_DISTANCE
                val distanceMin = (distanceLimit - DYNAMIC_RANGE) / distanceLimit

                val entity = event.entity
                val dRef = entity.position
                val height = entity.eyeHeight

                // Refine y position.
                // TODO: Make a little more accurate by counting in the actual bounding box.
                val pY = player.y + player.eyeHeight
                val dY = dRef.y
                if (pY <= dY)
                else if (pY >= dY + height)
                    dRef.y = dY + height
                else
                    dRef.y = pY// Keep the foot level y.
                // Level with damaged.

                val pL = player.location
                pL.y = pY
                val pRel = dRef.subtract(pL) // TODO: Run calculations on numbers only :p.
                // Distance is calculated from eye location to center of targeted. If the player is further away from their target
                // than allowed, the difference will be assigned to "distance".
                val lenpRel = pRel.length()

                val violation = lenpRel - distanceLimit
                val reachMod = checkData.reachMod

                if (violation > 0) {
                    if (checkData.playerCheat(checkData.fightReachVL,violation,"NCP Check #9")) checkData.fightReachVL += violation
                    checkDebug(player, "RC+: Reach CHECKED 1 vl=$violation dist=$lenpRel @$reachMod vlTotal=${checkData.fightReachVL}")
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                } else if (violation * reachMod > 0) {
                    if (checkData.playerCheat(checkData.fightReachVL,(violation * reachMod) / 4.toDouble(),"NCP Check #9")) checkData.fightReachVL += (violation * reachMod).toFloat() / 4f
                    checkDebug(player, "RC+: Reach CHECKED 2 vl=$violation dist=$lenpRel @$reachMod vlTotal=${checkData.fightReachVL}")
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                } else {
                    // Player passed the check, reward them.
                    checkData.fightReachVL *= 0.9
                }

                if (!df.defaultConfig[ConfigPaths.CHECKS_FIGHT_REACH_DYNAMICCHECK].toString().toBoolean()) {
                    checkData.reachMod = 1.0
                } else if (lenpRel > distanceLimit - DYNAMIC_RANGE) {
                    checkData.reachMod = Math.max(distanceMin, checkData.reachMod - DYNAMIC_STEP)
                } else {
                    checkData.reachMod = Math.min(1.0, checkData.reachMod + DYNAMIC_STEP)
                }

                //checkDebug(player,"NC+: Attack/reach $entity height=$height dist=$lenpRel @$reachMod")

            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}