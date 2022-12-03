package top.wetabq.wac.checks.block.blockplace.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockPlaceEvent
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.block.blockplace.BlockPlaceData
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.checks.utils.BlockUtils
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
class WrongPlace : Check<BlockPlaceData>() {


    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.setModuleName(DefaultModuleName.WRONGPLACE)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.BLOCKPLACE_WRONGPLACE
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is BlockPlaceEvent && checkData is BlockPlaceData) {
            val blockLimit = df.defaultConfig[ConfigPaths.CHECKS_BREAK_WRONGBREAK_BLOCKS].toString().toInt()

            val loc = player.location
            loc.y = loc.getY() + player.eyeHeight
            val blockList = BlockUtils.getLineBlock(loc,event.block)

            if (blockList.size >= blockLimit) {
                // They failed, increment violation level.
                checkData.wrongPlaceVL += blockList.size - blockLimit
                checkData.playerCheat(checkData.wrongPlaceVL,blockList.size - blockLimit.toDouble(),"WAC Check #5")
                // Remember how much further than allowed he tried to cheat for logging, if necessary.
                checkData.lastBlocks = blockList.size

            } else {
                // Player passed the check, reward them.
                checkData.wrongPlaceVL *= 0.9
            }
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}