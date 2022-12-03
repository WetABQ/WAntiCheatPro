package top.wetabq.wac.config

import top.wetabq.wac.config.reg.RegConfigContext
import top.wetabq.wac.config.reg.RegConfigModule
import top.wetabq.wac.module.WACModule
import top.wetabq.wac.module.exception.RegModuleException

/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class WAConfig : WACModule<RegConfigModule,RegConfigContext>() {

    override fun getRegisterInstance(): RegConfigModule {
        return RegConfigModule(this)
    }

    override fun <T> register(registry: T) {
        if (registry is RegConfigModule) else throw RegModuleException(this,"registry isn't RegLogModule")
    }

    override fun disable() {
        //(context as RegConfigContext).bindConfig?.save()
    }

    override fun context(registerContext: RegConfigContext) {
        this.context = registerContext
    }

}