package top.wetabq.wac.checks.extra

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.entity.Attribute
import cn.nukkit.entity.Entity
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.item.Item
import cn.nukkit.level.Location
import cn.nukkit.network.SourceInterface
import cn.nukkit.network.protocol.AnimatePacket
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket
import cn.nukkit.network.protocol.UpdateAttributesPacket
import cn.nukkit.scheduler.AsyncTask
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.fight.FightCheckData
import top.wetabq.wac.checks.fight.checks.AutoAim
import top.wetabq.wac.checks.utils.MathUtils
import top.wetabq.wac.checks.utils.NoCheckUtils
import top.wetabq.wac.module.DefaultModuleName
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.*

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class FakePlayerBot(interfaz: SourceInterface,clientId: Long,ip: InetAddress,port: Int,val targetPlayer: Player) : Player(interfaz,clientId, InetSocketAddress(ip,port)) {

    var lifeTick: Long = 20*8
    var attackedCount = 0
    var moveThread = false

    override fun getGravity(): Float {
        return 0.092f
    }

    override fun getStepHeight(): Double {
        return 0.0
    }

    override fun processLogin() {
        super.processLogin()
        val responsePacket = ResourcePackClientResponsePacket()
        responsePacket.packEntries = arrayOf()
        responsePacket.responseStatus = ResourcePackClientResponsePacket.STATUS_COMPLETED
        this.handleDataPacket(responsePacket)
    }

    override fun onUpdate(currentTick: Int): Boolean {
        if (lifeTick == 20*8L) {
            NoCheckUtils.setAllClearAndNoCheck(this.player, 20 * 60 * 120)
            this.getInventory().helmet = nextValue(helmet)
            this.getInventory().chestplate = nextValue(chestplate)
            this.getInventory().leggings = nextValue(leggings)
            this.getInventory().boots = nextValue(boots)
            this.getInventory().itemInHand = nextValue(weapon)
            this.setSkin(targetPlayer.skin)
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
                        val circular = Location(targetPlayer.getX() + Math.cos((Server.getInstance().tick * 20 * 6).toDouble())*3.5,
                                targetPlayer.getY() + 0.8,
                                targetPlayer.getZ() + Math.sin((Server.getInstance().tick * 20 * 6).toDouble())*3.5,
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

    companion object {

        val weapon = arrayOf(Item.get(Item.WOODEN_SWORD),Item.get(Item.IRON_SWORD),Item.get(Item.GOLDEN_SWORD),Item.get(Item.DIAMOND_SWORD),Item.get(Item.IRON_AXE),Item.get(Item.DIAMOND_AXE))
        val helmet = arrayOf(Item.get(Item.LEATHER_CAP),Item.get(Item.IRON_HELMET),Item.get(Item.GOLD_HELMET),Item.get(Item.DIAMOND_HELMET),Item.get(0))
        val chestplate = arrayOf(Item.get(Item.LEATHER_TUNIC),Item.get(Item.IRON_CHESTPLATE),Item.get(Item.GOLD_CHESTPLATE),Item.get(Item.DIAMOND_CHESTPLATE),Item.get(0))
        val leggings = arrayOf(Item.get(Item.LEATHER_PANTS),Item.get(Item.IRON_LEGGINGS),Item.get(Item.GOLD_LEGGINGS),Item.get(Item.DIAMOND_LEGGINGS),Item.get(0))
        val boots = arrayOf(Item.get(Item.LEATHER_BOOTS),Item.get(Item.IRON_BOOTS),Item.get(Item.GOLD_BOOTS),Item.get(Item.DIAMOND_BOOTS),Item.get(0))

        fun <T> nextValue(array: Array<T>): T {
            assert(array.isEmpty())
            return array[Random().nextInt(array.size)]
        }

    }

    override fun setHealth(health: Float) {
        super.setHealth(health)
        val pk0 = UpdateAttributesPacket()
        pk0.entityId = this.getId()
        pk0.entries = arrayOf(Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.maxHealth.toFloat()).setValue(this.getHealth()))
        this.getLevel().addChunkPacket(this.chunk.x, this.chunk.z, pk0)
    }

    override fun setMaxHealth(maxHealth: Int) {
        super.setMaxHealth(maxHealth)
        if (this.getHealth() > maxHealth) this.health = maxHealth.toFloat()
        val pk0 = UpdateAttributesPacket()
        pk0.entityId = this.getId()
        pk0.entries = arrayOf(Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.maxHealth.toFloat()).setValue(this.getHealth()))
        this.getLevel().addChunkPacket(this.chunk.x, this.chunk.z, pk0)
    }

    override fun knockBack(attacker: Entity, damage: Double, x: Double, z: Double, base: Double) {
        super.knockBack(attacker, damage, x, z, 0.0)
    }

}