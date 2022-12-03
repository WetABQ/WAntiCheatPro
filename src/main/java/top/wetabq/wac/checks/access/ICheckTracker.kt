package top.wetabq.wac.checks.access

import java.util.*


/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
interface ICheckTracker<T> {

    fun getTrackerSize() : Int

    fun addT(t : T)

    fun getLast(): T

    fun getVariance(): Double

    fun getAverage(): Double

    fun isFull(): Boolean

    fun clearAll()

}