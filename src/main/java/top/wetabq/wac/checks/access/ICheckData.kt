package top.wetabq.wac.checks.access

import top.wetabq.wac.checks.CheckType

/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
interface ICheckData {

    fun playerCheat(vl: Double,diff: Double,extra: String): Boolean

    fun doCheck(checkType: CheckType) : Boolean

    fun setBack()

    fun setNeedCheck()

    fun setNoCheck(tick: Int)

}