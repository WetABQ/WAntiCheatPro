package top.wetabq.wac.checks.fight

import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class FightCheckModule : Check<FightCheckData>() { // Parent

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.bindListener(FightCheckListener())
                    .setModuleName(DefaultModuleName.FIGHTCHECKMODULE)
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.FIGHT
    }


}