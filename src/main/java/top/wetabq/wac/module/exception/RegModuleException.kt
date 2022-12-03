package top.wetabq.wac.module.exception

import top.wetabq.wac.module.WACModule

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegModuleException(module: WACModule<*, *>, reason: String) : ModuleException(module,reason)