package top.wetabq.wac.checks.block.blockbreak

import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class BlockBreakModule : Check<BlockBreakData>() { // Parent

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.bindListener(BlockBreakListener())
                    .setModuleName(DefaultModuleName.BLOCKBREAKMODULE)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.BLOCKBREAK
    }


}