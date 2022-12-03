package top.wetabq.wac.checks

import cn.nukkit.Player
import cn.nukkit.Server
import top.wetabq.wac.checks.access.ICheckData
import top.wetabq.wac.checks.access.ViolationProcess
import top.wetabq.wac.checks.access.ViolationType
import top.wetabq.wac.checks.event.CheckPlayerCheatEvent
import top.wetabq.wac.checks.utils.NoCheckUtils
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class CheckData(protected val p: Player,protected val check: Check<*>,protected val df: DefaultConfig) : ICheckData {
    var noCheck = System.currentTimeMillis()
    var warned = false
    var muted = false
    var kickCount = 0
    var banCount = 0

    override fun doCheck(checkType: CheckType) : Boolean {
        val event = CheckPlayerCheatEvent(p, checkType)
        Server.getInstance().pluginManager.callEvent(event)
        //if (p.gamemode != 0 && p.gamemode != 2) event.setCancelled()
        if (event.isCancelled) return false
        val diff = this.noCheck - System.currentTimeMillis()
        if (!NoCheckUtils.noCheckAll.containsKey(p.name)) NoCheckUtils.noCheckAll[p.name] = System.currentTimeMillis()
        return noCheck <= System.currentTimeMillis() && NoCheckUtils.noCheckAll[p.name]!! <= System.currentTimeMillis()
    }

    override fun playerCheat(vl: Double,diff: Double,extra: String): Boolean {
        val warningVL = df.defaultConfig[ConfigPaths.CHECKS+check.javaClass.name.toLowerCase()+ ConfigPaths.CHECKS_WARNINGVL].toString().toDouble()
        val muteVL = df.defaultConfig[ConfigPaths.CHECKS+check.javaClass.name.toLowerCase()+ ConfigPaths.CHECKS_MUTEVL].toString().toDouble()
        val kickVLL = df.defaultConfig[ConfigPaths.CHECKS+check.javaClass.name.toLowerCase()+ ConfigPaths.CHECKS_KICKVL].toString().toDouble()
        val banVLL = df.defaultConfig[ConfigPaths.CHECKS+check.javaClass.name.toLowerCase()+ ConfigPaths.CHECKS_BANVL].toString().toDouble()
        val kickVL = kickVLL * (kickCount + 1)
        val banVL = banVLL * (banCount + 1)
        if (vl > warningVL && !warned && warningVL > 0) {
            if (diff > warningVL) return false
            warned = true
            ViolationProcess(check,p, ViolationType.WARNING,extra).executeInBanWave()
        }
        if (vl > muteVL && !muted && muteVL > 0) {
            if (diff > muteVL) return false
            muted = true
            ViolationProcess(check,p, ViolationType.MUTE,extra).executeInBanWave()
        }
        if (vl > kickVL && kickVL > 0) {
            if (diff > kickVLL) return false
            kickCount++
            ViolationProcess(check,p, ViolationType.KICK,extra).executeInBanWave()

        }
        if (vl > banVL && banVL > 0) {
            if (diff > banVLL) return false
            banCount++
            ViolationProcess(check,p, ViolationType.BAN,extra).executeInBanWave()
        }
        return true
    }

    override fun setNeedCheck() {
        this.noCheck = System.currentTimeMillis()
    }

    override fun setNoCheck(tick: Int) {
        if (this.noCheck > System.currentTimeMillis() + tick * 50 && tick != 0) return
        this.noCheck = System.currentTimeMillis() + tick * 50
    }

}