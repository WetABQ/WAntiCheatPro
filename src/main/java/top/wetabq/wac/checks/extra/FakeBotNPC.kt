package top.wetabq.wac.checks.extra

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.level.Location
import cn.nukkit.network.protocol.AnimatePacket
import cn.nukkit.scheduler.AsyncTask
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.fight.FightCheckData
import top.wetabq.wac.checks.fight.checks.AutoAim
import top.wetabq.wac.checks.utils.MathUtils
import top.wetabq.wac.module.DefaultModuleName

/**
 * easecation-root
 *
 * @author WetABQ Copyright (c) 2019.08
 * @version 1.0
 */
class FakeBotNPC(val targetPlayer: Player) : HumanNPC(targetPlayer, BotHelper.getRandomEnglishName(),targetPlayer.skin) {

    var lifeTick: Long = 20*8
    var attackedCount = 0
    var moveThread = false

    override fun initEntity() {
        super.initEntity()
        this.getInventory().helmet = FakePlayerBot.nextValue(FakePlayerBot.helmet)
        this.getInventory().chestplate = FakePlayerBot.nextValue(FakePlayerBot.chestplate)
        this.getInventory().leggings = FakePlayerBot.nextValue(FakePlayerBot.leggings)
        this.getInventory().boots = FakePlayerBot.nextValue(FakePlayerBot.boots)
        this.getInventory().itemInHand = FakePlayerBot.nextValue(FakePlayerBot.weapon)
    }

    override fun onUpdate(currentTick: Int): Boolean {
        if (lifeTick == 20*8L) {
            //this.setSkin(targetPlayer.skin)
            this.isNameTagVisible = false
            this.isNameTagAlwaysVisible = false
        }
        super.onUpdate(currentTick)
        return true
    }

    override fun entityBaseTick(tickDiff: Int): Boolean {
        super.entityBaseTick(tickDiff)
        lifeTick--
        val swing = AnimatePacket()
        swing.action = AnimatePacket.Action.SWING_ARM
        swing.eid = this.id
        val bot = this
        if (!moveThread) {
            moveThread = true
            Server.getInstance().scheduler.scheduleAsyncTask(WAntiCheatPro.instance, object : AsyncTask() {
                override fun onRun() {
                    while (lifeTick > 0 && bot.isAlive) {
                        val pair = MathUtils.look(targetPlayer, bot)
                        val circular = Location(targetPlayer.getX() + Math.cos((Server.getInstance().tick * 2 * 6).toDouble())*3.6,
                                targetPlayer.getY() + 0.8,
                                targetPlayer.getZ() + Math.sin((Server.getInstance().tick * 2 * 6).toDouble())*3.6,
                                pair.first, pair.second)
                        bot.teleport(circular)
                        Thread.sleep(20)
                    }
                }

                override fun onCompletion(server: Server?) {
                    if (attackedCount > 6) {
                        val autoAim = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.AUTOAIM) as AutoAim?)
                        (autoAim?.getPlayerCheckData(targetPlayer) as FightCheckData?)?.let {
                            it.autoAimVL += attackedCount+5
                            it.playerCheat(it.autoAimVL,1.0,"WACBot Check")
                        }
                    }
                    bot.close()
                }
            })
        }
        targetPlayer.dataPacket(swing)
        return true
    }

    override fun onNPCUpdate(tick: Int) {

    }

    override fun attack(source: EntityDamageEvent): Boolean {
        if (source is EntityDamageByEntityEvent) {
            val damager = source.damager
            if (damager.name.equals(this.targetPlayer.name)) {
                this.attackedCount++
                //println("Attack fake bot attackedCount=$attackedCount")
            }
        }
        source.setCancelled()
        return true
    }

}