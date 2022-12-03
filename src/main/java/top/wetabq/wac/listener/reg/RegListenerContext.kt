package top.wetabq.wac.listener.reg

import cn.nukkit.event.Listener
import top.wetabq.wac.module.reg.RegisterContext


/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class RegListenerContext : RegisterContext() {

    val registeredListener = ArrayList<Listener>()

}