package top.wetabq.wac.checks.movement.checks

import cn.nukkit.AdventureSettings
import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerMoveEvent
import cn.nukkit.item.Item
import cn.nukkit.scheduler.AsyncTask
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.movement.MovingCheckData
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.group.RegGroupModule
import cn.nukkit.potion.Effect
import cn.nukkit.network.protocol.MobEffectPacket
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.fight.FightCheckData
import top.wetabq.wac.checks.fight.checks.Critical
import top.wetabq.wac.checks.utils.PlayerUtils
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.ModuleVersion
import java.util.*


/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class HighJump : Check<MovingCheckData>() {

    companion object {
        var effects: Array<Effect?>? = null
    }

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addAsyncTask(object : AsyncTask() {

                override fun onRun() {
                    while (true) {
                        Thread.sleep(250)
                        for (player in Server.getInstance().onlinePlayers.values) {
                            val haveEffectIds = HashSet<Int>()
                            haveEffectIds.let {
                                for (v in player.effects.values) {
                                    it.add(v.id)
                                }
                            }
                            if (effects == null) {
                                val clazz = Class.forName("cn.nukkit.potion.Effect")
                                val fields = clazz.getDeclaredField("effects")
                                fields.isAccessible = true
                                effects = fields.get(clazz) as Array<Effect?>
                            } else {
                                for (e in effects ?: arrayOf()) {
                                    if (e != null) {
                                        if (!haveEffectIds.contains(e.id)) {
                                            val pk = MobEffectPacket()
                                            pk.eid = player.id
                                            pk.effectId = e.id
                                            pk.eventId = MobEffectPacket.EVENT_REMOVE.toInt()
                                            player.dataPacket(pk)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            })
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_HIGHJUMP_JUMPHEIGHT, 1.8)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_HIGHJUMP_ATTACKTIME, 800L)
                    .setModuleName(DefaultModuleName.HIGHJUMP)
                    .setModuleVersion(ModuleVersion(1, 0, 3, 21))
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.MOVING_HIGHJUMP
    }

    private fun canCheck(player: Player, checkData: MovingCheckData): Boolean {
        var flag = true
        val speed = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.SPEED) as Speed?)
        val sendVar = ((speed?.getPlayerCheckData(player) as MovingCheckData?)?.movePacketTracker?.sendVariance
                ?: 1) as Double
        if (player.hasEffect(Effect.SLOW_FALLING) || player.hasEffect(Effect.LEVITATION) || sendVar > 30.toDouble() || player.level.getBlockIdAt(player.x.toInt(), player.y.toInt() - 1, player.z.toInt()) == Block.SLIME_BLOCK || player.level.getBlockIdAt(player.x.toInt(), player.y.toInt() - 2, player.z.toInt()) == Block.SLIME_BLOCK || player.level.getBlockIdAt(player.x.toInt(), player.y.toInt() - 3, player.z.toInt()) == Block.SLIME_BLOCK) {
            checkData.setJumpNoCheck(20 * 3)
            flag = false
        }
        if (player.isInsideOfWater || PlayerUtils.playerInWater(player)) {
            checkData.fall4block = null
            checkData.lowestY = player.y
            checkData.lastOnGround = player.location
            checkData.lastJumpLocation = player.location
            checkDebug(player, "Clean HJ Data!!")
            checkData.setJumpNoCheck(20 * 5)
        }
        if (!(checkData.doJumpCheck() && (player.isSurvival || player.isAdventure) && player.inventory.chestplate.id != Item.ELYTRA && !player.isSleeping && player.riding == null && player.y >= 0 && !player.adventureSettings.get(AdventureSettings.Type.ALLOW_FLIGHT))) {
            checkData.fall4block = null
            checkData.lowestY = player.y
            checkData.lastOnGround = player.location
            checkData.lastJumpLocation = player.location
            checkDebug(player, "Clean HJ Data!!")
            flag = false
        }
        return flag
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig): Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is PlayerMoveEvent && checkData is MovingCheckData) {
            /**
             * WAC CHECK from Fecraft&WAC
             */
            val heightLimit = df.defaultConfig[ConfigPaths.CHECKS_MOVING_HIGHJUMP_JUMPHEIGHT].toString().toDouble()
            if (checkData.isAttcked()) return true
            if (!PlayerUtils.playerInAir(player) || !canCheck(player, checkData)) {
                checkData.fall4block = null
                checkData.lowestY = player.y
                checkData.lastHighestY = event.to.getY()
                checkData.lastOnGround = player.location
                checkData.lastJumpLocation = player.location
                checkDebug(player, "Clean HJ Data!!")
            } else {
                if (player.y - checkData.lowestY > heightLimit && !player.hasEffect(Effect.JUMP) && canCheck(player, checkData) && player.inAirTicks > 3) {
                    checkData.setNoCheck(5)
                    val diff = (player.y - checkData.lowestY) - heightLimit
                    if (checkData.playerCheat(checkData.highJumpVL, diff, "WAC Check #10")) checkData.highJumpVL += diff
                    checkDebug(player, "HJ+: CHECKED 1 vl=${(player.y - checkData.lowestY) - heightLimit} totalVl=${checkData.highJumpVL}")
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                } else {
                    //reduce VL
                    checkData.highJumpVL *= 0.98
                }

                val movingFly = getWACInstance().moduleManager.getModule(DefaultModuleName.MOVINGFLY) as MovingFly
                movingFly.checkCheat(player, checkData, event, df)
            }
            if (player.getY() < checkData.lowestY) {
                checkData.lowestY = player.getY()
            }
            if (player.getY() > checkData.lastHighestY) {
                checkData.lastHighestY = player.getY()
            }
            if (player.onGround || PlayerUtils.playerInLadder(player) || !canCheck(player, checkData)) {
                checkData.lastJumpLocation = player.location
            }
            if (checkData.lastOnGround != null && PlayerUtils.playerInAir(player)) {
                val critical = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.CRITICAL) as Critical?)
                val fightCheckData = (critical?.getPlayerCheckData(player) as FightCheckData?)
                if (fightCheckData?.startJump!!) {
                    if (player.hasEffect(Effect.JUMP)) {
                        var jumpHeight = Math.pow(player.getEffect(Effect.JUMP).amplifier + 4.2, 2.toDouble()) / 16.toDouble()
                        jumpHeight += heightLimit
                        val realHeight = player.y - checkData.lastJumpLocation.y
                        if (realHeight > jumpHeight && canCheck(player, checkData)) {
                            val diff = realHeight - jumpHeight
                            if (checkData.playerCheat(checkData.highJumpVL, diff, "WAC Check #10")) checkData.highJumpVL += diff
                            checkDebug(player, "HJ+: CHECKED 2 vl=${realHeight - jumpHeight} totalVl=${checkData.highJumpVL}")
                            if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                        } else {
                            //reduce VL
                            checkData.highJumpVL *= 0.98
                        }
                    } else {
                        if (canCheck(player, checkData) && player.y - checkData.lastJumpLocation.y > heightLimit) {
                            val diff = (player.y - checkData.lastJumpLocation.y) - heightLimit
                            if (checkData.playerCheat(checkData.highJumpVL, diff, "WAC Check #10")) checkData.highJumpVL += diff
                            checkDebug(player, "HJ+: CHECKED 3 vl=${(player.y - checkData.lastJumpLocation.y) - heightLimit} totalVl=${checkData.highJumpVL}")
                            if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                        } else {
                            checkData.highJumpVL *= 0.98
                        }
                    }
                } else {
                    if (canCheck(player, checkData) && !PlayerUtils.playerInLadder(player)) {
                        if (player.y - checkData.lastJumpLocation.y > 0) {
                            val diff = (player.y - checkData.lastJumpLocation.y) * 2f
                            if (checkData.playerCheat(checkData.highJumpVL, diff, "WAC Check #10")) checkData.highJumpVL += diff
                            checkDebug(player, "HJ+: CHECKED vl=${player.y - checkData.lastJumpLocation.y} totalVl=${checkData.highJumpVL}")
                            if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                        } else {
                            checkData.highJumpVL *= 0.98
                        }
                    } else {
                        checkData.highJumpVL *= 0.99
                    }
                }
            }

        } else throw CheckCheatException(this, "Incoming parameter error")
        return true
    }

}