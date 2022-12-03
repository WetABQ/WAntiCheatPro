package top.wetabq.wac.module.exception

import top.wetabq.wac.module.WACModule
import java.lang.RuntimeException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class ModuleException(val module: WACModule<*,*>?,reason: String) : RuntimeException(reason)