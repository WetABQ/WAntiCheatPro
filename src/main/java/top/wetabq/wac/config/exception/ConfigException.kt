package top.wetabq.wac.config.exception

import top.wetabq.wac.module.reg.RegisterContext
import java.lang.RuntimeException

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class ConfigException(reason: String,val configContext: RegisterContext) : RuntimeException(reason)