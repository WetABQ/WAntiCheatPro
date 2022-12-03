package top.wetabq.wac.checks.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar





/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
object DateUtils {

    @JvmStatic
    fun getNowDateStr() : String {
        val date = Date()
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return format.format(date)
    }

    @JvmStatic
    fun getStartDate(dateString: String) : Date {
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return format.parse(dateString)
    }

    @JvmStatic
    fun getDateStr(date: Date): String {
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return format.format(date)
    }

    @JvmStatic
    fun getEndDate(date:Date,days: Int) : Date {
        return Date(date.time + days * 24 * 60 * 60 * 1000)
    }

    @JvmStatic
    fun getEndSDate(date:Date,s: Int) : Date {
        return Date(date.time + s * 1000)
    }

    @JvmStatic
    fun isEffectiveDate(nowTime: Date, startTime: Date, endTime: Date): Boolean {
        if (nowTime.time == startTime.time || nowTime.time == endTime.time) return true
        val date = Calendar.getInstance()
        date.time = nowTime
        val begin = Calendar.getInstance()
        begin.time = startTime
        val end = Calendar.getInstance()
        end.time = endTime
        return date.after(begin) && date.before(end)
    }

}