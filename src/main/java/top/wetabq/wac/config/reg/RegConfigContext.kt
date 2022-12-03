package top.wetabq.wac.config.reg

import top.wetabq.wac.config.WConfig
import top.wetabq.wac.module.reg.RegisterContext

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegConfigContext : RegisterContext() {

    var bindConfig: WConfig? = null
    var configPaths = ArrayList<String>()

}