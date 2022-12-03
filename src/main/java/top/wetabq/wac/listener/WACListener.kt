package top.wetabq.wac.listener

import top.wetabq.wac.listener.reg.RegListenerContext
import top.wetabq.wac.listener.reg.RegListenerModule
import top.wetabq.wac.module.WACModule
import top.wetabq.wac.module.exception.RegModuleException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class WACListener : WACModule<RegListenerModule, RegListenerContext>() {

    override fun getRegisterInstance(): RegListenerModule {
        return RegListenerModule(this)
    }

    override fun <T> register(registry: T) {
        if (registry is RegListenerModule) else throw RegModuleException(this,"registry isn't RegCheckModule")
    }

    override fun context(registerContext: RegListenerContext) {
        context = registerContext
    }

}