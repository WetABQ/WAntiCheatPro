package top.wetabq.wac.checks.access

import top.wetabq.wac.checks.CheckType

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class ViolationData(val violationId : String,var violationDuration : Int,var violationType: ViolationType,
                    private val checkType : CheckType,val time : String,val extra : String) {

    fun toMap() : Map<String,String> {
        return  hashMapOf("violationId" to violationId,
                "violationDuration" to violationDuration.toString(),
                "violationType" to violationType.toString(),
                "checkType" to checkType.getName(),
                "time" to time,
                "extra" to extra)
    }

    fun getCheckType() : CheckType {
        return checkType
    }

    override fun toString(): String {
        return toMap().toString()
    }

}