package top.wetabq.wac.checks.block.blockbreak.checks

import cn.nukkit.Player
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
class WrongBreak : Check<BlockBreakData>() {


    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_BREAK_WRONGBREAK_BLOCKS,3)
                    .setModuleName(DefaultModuleName.WRONGBREAK)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.BLOCKBREAK_WRONGBREAK
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is BlockBreakEvent && checkData is BlockBreakData) {
            val blockLimit = df.defaultConfig[ConfigPaths.CHECKS_BREAK_WRONGBREAK_BLOCKS].toString().toInt()

            val loc = player.location
            loc.y = loc.getY() + player.eyeHeight
            val blockList = BlockUtils.getLineBlock(loc,event.block)

            if (blockList.size >= blockLimit) {
                // They failed, increment violation level.
                if (checkData.playerCheat(checkData.wrongBreakVL,(blockList.size - blockLimit).toDouble(),"WAC Check #3")) checkData.wrongBreakVL += blockList.size - blockLimit
                // Remember how much further than allowed he tried to cheat for logging, if necessary.
                checkData.lastBlocks = blockList.size
                if (df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.lowercase() + ConfigPaths.CHECKS_CANCELEVENT].toString().toBoolean()) event.setCancelled()
            } else {
                // Player passed the check, reward them.
                checkData.wrongBreakVL *= 0.9
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}