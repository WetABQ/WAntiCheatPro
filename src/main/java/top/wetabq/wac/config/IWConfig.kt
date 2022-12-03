package top.wetabq.wac.config

/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
interface IWConfig {
    fun init()

    fun spawnDefaultConfig()

    fun save()
}