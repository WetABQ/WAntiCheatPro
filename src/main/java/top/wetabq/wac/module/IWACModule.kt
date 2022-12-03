package top.wetabq.wac.module

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
interface IWACModule {

    fun <T> register(registry: T)

    fun disable()

}