package top.wetabq.wac.checks.fight.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.fight.FightCheckData
import top.wetabq.wac.checks.utils.MathUtils
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule
import kotlin.math.abs

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class AutoAim : Check<FightCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry//.addConfigPath(ConfigPaths.CHECKS_FIGHT_AUTOAIM_TRACKERSIZE,10)
                    .addConfigPath(ConfigPaths.CHECKS_FIGHT_AUTOAIM_DYNAMICRANGE_YAW,5.0)
                    .addConfigPath(ConfigPaths.CHECKS_FIGHT_AUTOAIM_DYNAMICRANGE_PITCH,8.0)
                    .setModuleName(DefaultModuleName.AUTOAIM)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.FIGHT_AUTOAIM
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is EntityDamageByEntityEvent && checkData is FightCheckData) {
            if (event.isCancelled) return true
            if (player.isImmobile)  {
                checkData.setNoCheck(3*20)
                return true
            }
            val damager = event.damager
            //TODO: 整理 合并代码 优化
            if (damager is Player && event.entity is Player && event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                val yp = MathUtils.look(event.entity, damager)
                val yp2 = MathUtils.look2(event.entity as Player, damager)
                val dRYaw = df.defaultConfig[ConfigPaths.CHECKS_FIGHT_AUTOAIM_DYNAMICRANGE_YAW].toString().toDouble()
                val dRPitch = df.defaultConfig[ConfigPaths.CHECKS_FIGHT_AUTOAIM_DYNAMICRANGE_PITCH].toString().toDouble()
                //checkDebug(player, "AA+: AutoAim Range1=${MathUtils.inRange(yp.first, dR, damager.yaw)} Range2=${MathUtils.inRange(yp.second, dR, damager.pitch)} Range3=${MathUtils.inRange(yp2.first, dR, damager.yaw)} Range4=${MathUtils.inRange(yp2.second, dR, damager.pitch)} Range5=${MathUtils.inRange(yp.first, dR, damager.yaw-360)} Range6=${MathUtils.inRange(yp2.first, dR, damager.yaw-360)}")
                //checkDebug(player, "AA+: AutoAim diff[yaw=${abs(yp2.first - player.yaw)} pitch=${abs(yp2.second - player.pitch)} yaw''=${yp2.first - (player.yaw-360)}]")
                if (MathUtils.inRange(yp.first, dRYaw, damager.yaw) && MathUtils.inRange(yp.second, dRPitch, damager.pitch)) {
                    var diff = abs(yp.first - damager.yaw) + abs(yp.second - damager.pitch)*0.8
                    if (diff <= 0) diff = 0.3
                    var vl = 2.toDouble() / diff
                    if (vl > 10) vl = 10.0
                    checkData.autoAimVL += vl
                    checkData.playerCheat(checkData.autoAimVL, vl,"WAC Check #8")
                    checkDebug(player, "AA+: AutoAim CHECKED vl=$vl diff=$diff vlTotal=${checkData.autoAimVL}")
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                }else if(MathUtils.inRange(yp2.first, dRYaw, damager.yaw) && MathUtils.inRange(yp2.second, dRPitch, damager.pitch)) {
                    var diff = abs(yp2.first - damager.yaw) + abs(yp2.second - damager.pitch)*0.8
                    if (diff <= 0) diff = 0.3
                    var vl = 2.toDouble() / diff
                    if (vl > 10) vl = 10.0
                    checkData.autoAimVL += vl
                    checkData.playerCheat(checkData.autoAimVL, vl,"WAC Check #8")
                    checkDebug(player, "AA+: AutoAim CHECKED vl=$vl diff=$diff vlTotal=${checkData.autoAimVL}")
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                } else if (MathUtils.inRange(yp.first, dRYaw, damager.yaw-360) && MathUtils.inRange(yp.second, dRPitch, damager.pitch)){
                    var diff = abs(yp.first - (damager.yaw-360)) + abs(yp.second - damager.pitch)*0.8
                    if (diff <= 0) diff = 0.3
                    var vl = 3.toDouble() / diff
                    if (vl > 10) vl = 10.0
                    checkData.autoAimVL += vl
                    checkData.playerCheat(checkData.autoAimVL, vl,"WAC Check #8")
                    checkDebug(player, "AA+: AutoAim CHECKED vl=$vl diff=$diff vlTotal=${checkData.autoAimVL}")
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                } else if (MathUtils.inRange(yp2.first, dRYaw, damager.yaw-360) && MathUtils.inRange(yp2.second, dRPitch, damager.pitch)) {
                    var diff = abs(yp2.first - (damager.yaw-360)) + abs(yp2.second - damager.pitch)*0.8
                    if (diff <= 0) diff = 0.3
                    var vl = 2.toDouble() / diff
                    if (vl > 10) vl = 10.0
                    checkData.autoAimVL += vl
                    checkData.playerCheat(checkData.autoAimVL, vl,"WAC Check #8")
                    checkDebug(player, "AA+: AutoAim CHECKED vl=$vl diff=$diff vlTotal=${checkData.autoAimVL}")
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                } else {
                    checkData.autoAimVL *= 0.98
                }
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}