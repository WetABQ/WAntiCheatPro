package top.wetabq.wac.module.group

import top.wetabq.wac.module.WACModule
import top.wetabq.wac.module.exception.RegModuleException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class GroupModule : WACModule<RegGroupModule,RegGroupContext>() {

    override fun getRegisterInstance(): RegGroupModule {
        return RegGroupModule(this)
    }

    override fun <T> register(registry: T) {
        if (registry is RegGroupModule) else throw RegModuleException(this,"registry isn't RegGroupModule")
    }

    override fun disable() {
        (context as RegGroupContext).bindConfig?.save()
    }

    override fun context(registerContext: RegGroupContext) {
        this.context = registerContext
    }

}