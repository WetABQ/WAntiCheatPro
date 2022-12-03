package top.wetabq.wac.checks.block.blockbreak.checks

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockBreakEvent
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.block.blockbreak.BlockBreakData
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.utils.BlockUtils
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class FastBreak : Check<BlockBreakData>() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_BREAK_FASTBREAK_FASTBREAKDELAY,100)
                    .setModuleName(DefaultModuleName.FASTBREAK)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.BLOCKBREAK_FASTBREAK
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig): Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is BlockBreakEvent && checkData is BlockBreakData) {
            if (player.isCreative) return true
            var expectedBreakingTime = BlockUtils.getBreakTime(player.inventory.itemInHand,player,event.block)*1000
            val elapsedTime = System.currentTimeMillis() - checkData.lastBreak
            if (player.inventory.itemInHand.isSword || event.block.id == Block.GLOWING_OBSIDIAN) expectedBreakingTime = 100.0 //剑类误判
            if (elapsedTime < 0) {
                // Ignore it. TODO: ?
            } else if (elapsedTime + df.defaultConfig[ConfigPaths.CHECKS_BREAK_FASTBREAK_FASTBREAKDELAY].toString().toLong() < expectedBreakingTime) {
                // lag or cheat or Minecraft.

                //TODO: 添加服务器延迟检测
                // Count in server side lag, if desired.
                val lag = 1f

                val missingTime = expectedBreakingTime - (lag * elapsedTime).toLong()

                if (missingTime > 0) {
                    // Add as penalty
                    // TODO: maybe add one absolute penalty time for big amounts to stop breaking until then
                    val vlAdded = missingTime / 1000.0
                    if (checkData.playerCheat(checkData.fastBreakVL,vlAdded,"NCP + Nukkit Check #1")) checkData.fastBreakVL += vlAdded
                    if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.lowercase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
                    // else: still within contention limits.
                }
            } else if (expectedBreakingTime > df.defaultConfig[ConfigPaths.CHECKS_BREAK_FASTBREAK_FASTBREAKDELAY].toString().toLong()) {
                checkData.fastBreakVL *= 0.9
            }

            tailDebugStats(player, event.block, elapsedTime, expectedBreakingTime)
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

    private fun tailDebugStats(player: Player, block: Block, elapsedTime: Long, expectedBreakingTime: Double) {
        val msg =  "[" + block + "] " + elapsedTime + "u / " + expectedBreakingTime + "r"
        checkDebug(player,msg)
    }

}