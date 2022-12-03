package top.wetabq.wac.config.exception

import top.wetabq.wac.module.reg.RegisterContext

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class ConfigLoadingException(reason: String,configContext: RegisterContext) : ConfigException(reason,configContext)