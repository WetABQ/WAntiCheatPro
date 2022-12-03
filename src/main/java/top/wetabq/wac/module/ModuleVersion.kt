package top.wetabq.wac.module

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class ModuleVersion(private val v1: Int,private val v2: Int,private val v3: Int,private val buildNumber: Int) {
    override fun toString(): String {
        return "v$v1.$v2.${v3}_b$buildNumber"
    }
}