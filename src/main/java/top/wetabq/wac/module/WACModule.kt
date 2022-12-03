package top.wetabq.wac.module

import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.module.reg.RegisterContext
import top.wetabq.wac.module.reg.RegisterModule

/**
 * WAntiCheatPro
 *
 * (WACModule - RegisterModule - RegisterContext) -> a chuck module
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */

abstract class WACModule<out R : RegisterModule,in K : RegisterContext> : IWACModule {

    protected lateinit var context: RegisterContext

    abstract fun getRegisterInstance() : R

    abstract fun context(registerContext: K)

    override fun disable() {

    }

    fun getWACInstance() : WAntiCheatPro {
        return WAntiCheatPro.instance
    }

    fun getModuleContext() : RegisterContext {
        return context
    }

}