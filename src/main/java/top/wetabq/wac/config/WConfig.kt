package top.wetabq.wac.config

import cn.nukkit.utils.Config
import cn.nukkit.utils.ConfigSection
import top.wetabq.wac.WAntiCheatPro

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class WConfig(configName:String) : IWConfig {
    var config: Config
    var configSection: ConfigSection

    init {
        this.config = Config(  "${WAntiCheatPro.instance.dataFolder}/$configName.yml", Config.YAML)
        this.configSection = config.rootSection
    }

    override fun save() {
        config.setAll(configSection)
        config.save()
    }

    override fun spawnDefaultConfig() {

    }

    override fun init() {

    }

    fun isEmpty(): Boolean {
        return configSection.isEmpty()
    }
}