package top.wetabq.wac.logging.module

import top.wetabq.wac.config.WAConfig
import top.wetabq.wac.config.WConfig
import top.wetabq.wac.config.reg.RegConfigContext
import top.wetabq.wac.config.reg.RegConfigModule
import top.wetabq.wac.module.ModuleVersion

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class LogRecordConfig : WAConfig() {

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegConfigModule) {
            registry.bindConfig(object :WConfig("logRecord") {})
                    .setModuleName("LogRecordConfig")
                    .setModuleAuthor("WetABQ")
                    .setModuleVersion(ModuleVersion(1,0,0,1))
                    .context()
        }
    }

    fun addLogRecord(record: String) {
        if (context is RegConfigContext) {
            val configSection = (context as RegConfigContext).bindConfig!!.configSection
            val id = configSection.size + 1
            configSection["#$id"] = record
            (context as RegConfigContext).bindConfig?.save()
        }
    }

}