package top.wetabq.wac.module.group

import cn.nukkit.Server
import cn.nukkit.event.Listener
import cn.nukkit.plugin.Plugin
import cn.nukkit.scheduler.AsyncTask
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.command.WCommand
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.config.WConfig
import top.wetabq.wac.config.reg.RegConfigContext
import top.wetabq.wac.module.WACModule
import top.wetabq.wac.module.reg.RegisterModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegGroupModule(private val module: WACModule<RegGroupModule,RegGroupContext>) : RegisterModule(module) {

    init {
        context = RegGroupContext()
    }

    fun bindListener(listener: Listener, plugin: Plugin): RegGroupModule {
        if (!(context as RegGroupContext).registeredListener.contains(listener)) {
            (context as RegGroupContext).registeredListener.add(listener)
            WAntiCheatPro.instance.server.pluginManager.registerEvents(listener, plugin)
        }
        return this
    }

    fun bindListener(listener: Listener): RegGroupModule {
        bindListener(listener, WAntiCheatPro.instance)
        return this
    }

    fun bindConfig(config: WConfig) : RegGroupModule {
        (context as RegGroupContext).bindConfig = config
        return this
    }

    fun addConfigPath(configPath: String,defaultValue: Any) : RegGroupModule {
        (context as RegGroupContext).configPaths.add(configPath)
        val defaultConfig = WAntiCheatPro.df.defaultConfig
        if (!defaultConfig.containsKey(configPath)) {
            defaultConfig[configPath] = defaultValue
            (WAntiCheatPro.df.getModuleContext() as RegConfigContext).bindConfig?.save()
        }
        return this
    }

    fun addCommand(subCommand: WSubCommand) : RegGroupModule {
        (context as RegGroupContext).bindCommands.add(subCommand)
        return this
    }

    fun setCommandPrefix(prefix: String) : RegGroupModule {
        (context as RegGroupContext).commandPrefix = prefix
        return this
    }


    fun registerCommands() : RegGroupModule { //Must use
        (context as RegGroupContext).bindCommands.forEach { cmd -> WCommand.subCommand.add(cmd) }
        return this
    }

    fun addAsyncTask(plugin: Plugin,task: AsyncTask) : RegGroupModule {
        Server.getInstance().scheduler.scheduleAsyncTask(plugin, task)
        (context as RegGroupContext).asyncTasks.add(task)
        return this
    }

    fun addAsyncTask(task: AsyncTask) : RegGroupModule {
        addAsyncTask(WAntiCheatPro.instance,task)
        return this
    }

    override fun context() {
        module.context(context as RegGroupContext)
    }
}