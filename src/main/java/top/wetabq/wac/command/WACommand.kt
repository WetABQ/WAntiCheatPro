package top.wetabq.wac.command

import top.wetabq.wac.command.reg.RegCommandContext
import top.wetabq.wac.command.reg.RegCommandModule
import top.wetabq.wac.module.WACModule
import top.wetabq.wac.module.exception.RegModuleException


/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class WACommand : WACModule<RegCommandModule,RegCommandContext>(){

    companion object {
        var wCommand: WCommand? = null
    }

    override fun getRegisterInstance(): RegCommandModule {
        return RegCommandModule(this)
    }

    override fun <T> register(registry: T) {
        if (registry is RegCommandModule) else throw RegModuleException(this,"registry isn't RegCommandModule")
    }

    override fun context(registerContext: RegCommandContext) {
        this.context = registerContext
    }

}