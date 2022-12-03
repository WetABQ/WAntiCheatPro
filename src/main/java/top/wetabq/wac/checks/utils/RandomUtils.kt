package top.wetabq.wac.checks.utils

import java.util.*

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
object RandomUtils {

    @JvmStatic
    fun r(min: Int, max: Int): Int {
        val rand = Random()
        return rand.nextInt(max - min + 1) + min
    }

}