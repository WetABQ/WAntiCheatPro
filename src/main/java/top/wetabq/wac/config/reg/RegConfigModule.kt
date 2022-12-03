package top.wetabq.wac.config.reg

import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.config.WAConfig
import top.wetabq.wac.config.WConfig
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.reg.RegisterModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegConfigModule(private val module: WAConfig) : RegisterModule(module) {

    init {
        context = RegConfigContext()
    }

    fun bindConfig(config: WConfig) : RegConfigModule {
        (context as RegConfigContext).bindConfig = config
        return this
    }

    fun addConfigPath(configPath: String,defaultValue: Any) : RegConfigModule {
        (context as RegConfigContext).configPaths.add(configPath)
        val defaultConfig = WAntiCheatPro.df.defaultConfig
        if (!defaultConfig.containsKey(configPath)) {
            defaultConfig[configPath] = defaultValue
            (WAntiCheatPro.df.getModuleContext() as RegConfigContext).bindConfig?.save()
        }
        return this
    }

    override fun context() {
        module.context(context as RegConfigContext)
    }

}