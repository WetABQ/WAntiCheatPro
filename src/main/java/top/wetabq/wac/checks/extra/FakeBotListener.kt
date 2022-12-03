package top.wetabq.wac.checks.extra

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class FakeBotListener : Listener {

    @EventHandler
    fun attackBot(event: EntityDamageEvent) {
        if (event is EntityDamageByEntityEvent) {
            val entity = event.entity
            if (entity is FakePlayerBot) {
                val damager = event.damager
                if (damager.name.equals(entity.targetPlayer.name)) {
                    entity.attackedCount++
                }
                event.setCancelled()
            }
        }
    }

}