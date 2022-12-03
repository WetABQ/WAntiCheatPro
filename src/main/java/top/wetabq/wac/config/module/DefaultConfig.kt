package top.wetabq.wac.config.module

import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.WAConfig
import top.wetabq.wac.config.WConfig
import top.wetabq.wac.config.exception.ConfigLoadingException
import top.wetabq.wac.config.reg.RegConfigModule
import top.wetabq.wac.module.ModuleVersion

/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class DefaultConfig : WAConfig() {

    var defaultConfig = LinkedHashMap<String,Any>()

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegConfigModule) {
            registry.bindConfig(object: WConfig("waconfig"){
                init {
                    init()
                }
                override fun init() {
                    if (!isEmpty()) {
                        try {
                            defaultConfig = configSection["wac"] as LinkedHashMap<String, Any>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            throw ConfigLoadingException("can't init defaultConfig",context)
                        }
                    } else {
                        spawnDefaultConfig()
                    }
                }

                override fun spawnDefaultConfig() {
                    if (isEmpty()) {
                        // ** DEFAULT CONFIG **
                        defaultConfig[ConfigPaths.LOGGING_CHECKLOG_CONSOLELOG] = true
                        defaultConfig[ConfigPaths.LOGGING_CHECKLOG_PREFIX] = "WACheck"
                        defaultConfig[ConfigPaths.LOGGING_CHECKLOG_PRINTTIME] = true
                        defaultConfig[ConfigPaths.LOGGING_CHECKLOG_RECORDLOG] = true
                        defaultConfig[ConfigPaths.DEBUGMODE] = false
                        configSection["wac"] = defaultConfig
                    }
                    init()
                    save()
                }

                override fun save() {
                    if (!isEmpty()) {
                        try {
                            configSection.clear()
                            configSection["wac"] = defaultConfig
                            config.setAll(configSection)
                            config.save()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            throw ConfigLoadingException("can't save defaultConfig",context)
                        }
                    } else {
                        spawnDefaultConfig()
                    }
                }
            })
                    .setModuleName("DefaultConfig")
                    .setModuleAuthor("WetABQ")
                    .setModuleVersion(ModuleVersion(1,0,0,1))
                    .context()
        }
    }

}