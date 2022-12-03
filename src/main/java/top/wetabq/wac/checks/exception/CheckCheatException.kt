package top.wetabq.wac.checks.exception

import top.wetabq.wac.checks.Check

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class CheckCheatException(module: Check<*>, reason: String) : CheckException(module,reason)