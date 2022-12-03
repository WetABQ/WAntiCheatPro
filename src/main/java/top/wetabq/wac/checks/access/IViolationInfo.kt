package top.wetabq.wac.checks.access

import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData

/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
interface IViolationInfo<out D: CheckData> {

    fun getCheck(): Check<*>

    fun getExtra(): String

    fun getCheckData(): D

    fun executePunishment()

    fun isBanWave() : Boolean

    fun executeInBanWave()

    fun willCancel() : Boolean

    fun forceCancel()

    fun preventCancel()

}