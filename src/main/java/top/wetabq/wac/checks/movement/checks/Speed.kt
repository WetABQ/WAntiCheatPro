package top.wetabq.wac.checks.movement.checks

import cn.nukkit.AdventureSettings
import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.event.Event
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.item.Item
import cn.nukkit.network.protocol.MovePlayerPacket
import cn.nukkit.potion.Effect
import cn.nukkit.scheduler.AsyncTask
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.movement.MovingCheckData
import top.wetabq.wac.checks.utils.PlayerUtils
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.ModuleVersion
import top.wetabq.wac.module.group.RegGroupModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class Speed : Check<MovingCheckData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_TRACKERSIZE, 6)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXMOVEPACKET, 24)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXVARIANCE, 1)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SNEAKING, 0.44)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_WALK, 1.4)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SPRINT, 2.4)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SNEAKING_AVG, 0.06)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_WALK_AVG, 0.18)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SPRINT_AVG, 0.36)
                    //.addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_POSITION, 1.6)
                    .addConfigPath(ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_MAXVARIANCE,0.04)
                    /*.addAsyncTask(object : AsyncTask() {
                        override fun onRun() {
                            while (true) {
                                for (player in Server.getInstance().onlinePlayers.values) {
                                    if (player.loggedIn && player.position != null) {
                                        val checkData = getPlayerCheckData(player) as MovingCheckData
                                        checkData.positionTracker.addT(player.position)
                                        if (checkData.positionTracker.isFull()) {
                                            val avg = checkData.positionTracker.getAverage()
                                            val speedLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_POSITION].toString().toDouble()
                                            //println("DEBUG avg=$avg motionState=${checkData.motionState} v=${checkData.movePacketTracker.getVariance()} v2=${checkData.movePacketTracker.sendVariance}")
                                            if (avg > speedLimit && checkData.doCheck(getCheckType())) {
                                                //checkData.speedVL += avg - speedLimit
                                                //checkData.playerCheat(checkData.speedVL, "WAC Check #14")
                                            } else {
                                                //checkData.speedVL *= 0.9
                                            }
                                        }
                                    }
                                }
                                Thread.sleep(250)
                            }
                        }
                    })*/
                    .setModuleName(DefaultModuleName.SPEED)
                    .setModuleAuthor("WetABQ")
                    .setModuleVersion(ModuleVersion(1,0,2,1))
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.MOVING_SPEED
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (player.isCreative || player.isSpectator) return true
        if (checkData is MovingCheckData && event is DataPacketReceiveEvent) {
            val packet = event.packet
            val highJump = (WAntiCheatPro.instance.moduleManager.getModule(DefaultModuleName.HIGHJUMP) as HighJump?)
            if (packet is MovePlayerPacket && !player.isSwimming && !player.isInsideOfWater && !player.isSleeping && player.riding == null && (player.isSurvival || player.isAdventure) && checkData.movePacketTracker.isFull() && player.inventory.chestplate.id != Item.ELYTRA && !(highJump?.getPlayerCheckData(player) as MovingCheckData).isAttcked() && !player.hasEffect(Effect.SPEED) && !player.hasEffect(Effect.JUMP) && !player.adventureSettings.get(AdventureSettings.Type.ALLOW_FLIGHT)) {
                if (checkData.movePacketTracker.sendVariance > 30 || !player.onGround) return true
                val avg = checkData.movePacketTracker.getAverage()
                val varianceLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_MAXVARIANCE].toString().toDouble()
                val variance = checkData.movePacketTracker.getVariance()
                val sneakingSpeedLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SNEAKING].toString().toDouble()
                val sprintSpeedLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SPRINT].toString().toDouble()
                var walkSpeedLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_WALK].toString().toDouble()
                val sneakingAvgSpeedLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SNEAKING_AVG].toString().toDouble()
                val sprintAvgSpeedLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_SPRINT_AVG].toString().toDouble()
                var walkAvgSpeedLimit = WAntiCheatPro.df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXSPEED_PACKET_WALK_AVG].toString().toDouble()
                val instantSpeed = checkData.movePacketTracker.getInstant(packet)
                //println("DEBUG player=${player.name} avg=$avg instantSpeed=$instantSpeed motionState=${checkData.motionState} v=${checkData.movePacketTracker.getVariance()} v2=${checkData.movePacketTracker.sendVariance}")
                if (PlayerUtils.playerInS(player)) {
                    walkAvgSpeedLimit += 0.3
                    walkSpeedLimit += 0.4
                }
                if (instantSpeed > sneakingSpeedLimit && checkData.motionState == MovingCheckData.MotionState.SNEAKING && checkData.movePacketTracker.sendVariance < df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXVARIANCE].toString().toDouble()) {
                    if (checkData.playerCheat(checkData.speedVL, instantSpeed - sneakingSpeedLimit,"WAC Check #16")) checkData.speedVL += instantSpeed - sneakingSpeedLimit
                    checkDebug(player,"SD+: CHECKED 1 vl=${instantSpeed - sneakingSpeedLimit} totalVl=${checkData.speedVL}")
                } else if (instantSpeed > sprintSpeedLimit && checkData.motionState == MovingCheckData.MotionState.SPRINT && checkData.movePacketTracker.sendVariance < df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXVARIANCE].toString().toDouble()) {
                    if (checkData.playerCheat(checkData.speedVL, instantSpeed - sprintSpeedLimit,"WAC Check #17")) checkData.speedVL += instantSpeed - sprintSpeedLimit
                    checkDebug(player,"SD+: CHECKED 2 vl=${instantSpeed - sprintSpeedLimit} totalVl=${checkData.speedVL}")
                } else if (instantSpeed > walkSpeedLimit && checkData.motionState == MovingCheckData.MotionState.WALK && checkData.movePacketTracker.sendVariance < df.defaultConfig[ConfigPaths.CHECKS_MOVING_SPEED_MAXVARIANCE].toString().toDouble()) {
                    if (checkData.playerCheat(checkData.speedVL, instantSpeed - walkSpeedLimit,"WAC Check #18")) checkData.speedVL += instantSpeed - walkSpeedLimit
                    checkDebug(player,"SD+: CHECKED 3 vl=${instantSpeed - walkSpeedLimit} totalVl=${checkData.speedVL}")
                    player.isSprinting = false
                }else if (avg > sneakingAvgSpeedLimit && checkData.motionState == MovingCheckData.MotionState.SNEAKING && variance < varianceLimit) {
                    if (checkData.playerCheat(checkData.speedVL, avg - sneakingAvgSpeedLimit,"WAC Check #20")) checkData.speedVL += avg - sneakingAvgSpeedLimit
                    checkDebug(player,"SD+: CHECKED 4 vl=${avg - sneakingAvgSpeedLimit} totalVl=${checkData.speedVL}")
                } else if (avg > sprintAvgSpeedLimit && checkData.motionState == MovingCheckData.MotionState.SPRINT && variance < varianceLimit) {
                    if (checkData.playerCheat(checkData.speedVL, avg - sprintAvgSpeedLimit,"WAC Check #21")) checkData.speedVL += avg - sprintAvgSpeedLimit
                    checkDebug(player,"SD+: CHECKED 5 vl=${avg - sprintAvgSpeedLimit} totalVl=${checkData.speedVL}")
                } else if (avg > walkAvgSpeedLimit && checkData.motionState == MovingCheckData.MotionState.WALK && variance < varianceLimit) {
                    if (checkData.playerCheat(checkData.speedVL, avg - walkAvgSpeedLimit,"WAC Check #22")) checkData.speedVL += avg - walkAvgSpeedLimit
                    checkDebug(player,"SD+: CHECKED 6 vl=${avg - walkAvgSpeedLimit} totalVl=${checkData.speedVL}")
                    player.isSprinting = false
                } else {
                    checkData.speedVL *= 0.95
                }
            }
        } else throw CheckCheatException(this, "Incoming parameter error")
        return true
    }

}