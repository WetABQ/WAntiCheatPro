package top.wetabq.wac.checks.movement.checks

import cn.nukkit.AdventureSettings
import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.item.Item
import cn.nukkit.potion.Effect
import top.wetabq.wac.WAntiCheatPro
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
class MovingFly : Check<MovingCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.setModuleName(DefaultModuleName.MOVINGFLY)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.MOVING_SURVIVALFLY
    }

    private fun canCheck(player: Player):Boolean {
        val speed = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.SPEED) as Speed?)
        val sendVar = ((speed?.getPlayerCheckData(player) as MovingCheckData?)?.movePacketTracker?.sendVariance?:1) as Double
        return sendVar < 30 && (player.isSurvival || player.isAdventure) && player.inventory.chestplate.id != Item.ELYTRA && !player.isSleeping && player.riding == null && player.y >= 0 && !player.adventureSettings.get(AdventureSettings.Type.ALLOW_FLIGHT)
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        val highJump = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.HIGHJUMP) as HighJump?)
        val cd = highJump?.getPlayerCheckData(player) as MovingCheckData?
        if (cd is MovingCheckData) {
            //HighJump 调用 不然会出事
            val d2 = player.location
            val dy = player.location.y
            d2.y = 0.toDouble()
            val l2 = (cd.lastOnGround?:player.location).clone()
            l2.y = 0.toDouble()
            if (d2.distance(l2) > 4) {
                if (cd.fall4block == null) {
                    cd.fall4block = player.location
                } else {
                    val f2 = cd.fall4block
                    d2.y = dy
                    if (d2.distance(f2) > 1 && d2.distance(f2) * 0.8 > cd.fall4block!!.y - player.y && !player.hasEffect(Effect.JUMP) && canCheck(player)) {
                        cd.setNoCheck(5)
                        val diff = d2.distance(f2) * 0.8 - (cd.fall4block!!.y - player.y)
                        if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event?.setCancelled()
                        if (cd.playerCheat(cd.flyVL,diff,"WAC Check #11")) cd.flyVL += diff
                        checkDebug(player,"MF+: CHECKED vl=${d2.distance(f2) * 0.8 - (cd.fall4block!!.y - player.y)} totalVl=${cd.flyVL}")
                    } else {
                        cd.flyVL *= 0.9
                    }
                }
            } else {
                cd.flyVL *= 0.9
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}