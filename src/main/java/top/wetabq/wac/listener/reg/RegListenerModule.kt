package top.wetabq.wac.listener.reg

import cn.nukkit.event.Listener
import cn.nukkit.plugin.Plugin
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.listener.WACListener
import top.wetabq.wac.module.reg.RegisterModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegListenerModule(private val module: WACListener) : RegisterModule(module) {

    init {
        context = RegListenerContext()
    }

    fun bindListener(listener: Listener, plugin: Plugin): RegListenerModule {
        if (!(context as RegListenerContext).registeredListener.contains(listener)) {
            (context as RegListenerContext).registeredListener.add(listener)
            WAntiCheatPro.instance.server.pluginManager.registerEvents(listener, plugin)
        }
        return this
    }

    fun bindListener(listener: Listener): RegListenerModule {
        bindListener(listener,WAntiCheatPro.instance)
        return this
    }

    override fun context(){
        module.context(context as RegListenerContext)
    }

}