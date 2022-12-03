package top.wetabq.wac.checks.exception

import top.wetabq.wac.checks.Check
import top.wetabq.wac.module.exception.ModuleException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class CheckException(module: Check<*>,reason: String) : ModuleException(module,reason)