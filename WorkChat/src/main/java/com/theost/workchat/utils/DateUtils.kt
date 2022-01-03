package com.theost.workchat.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getDayDate(date: Date): String {
        return SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }

    fun getTime(date: Date): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    fun notSameDay(date1: Date, date2: Date): Boolean {
        val day1 = Calendar.getInstance().apply { time = date1 }
        val day2 = Calendar.getInstance().apply { time = date2 }
        return day1[Calendar.DAY_OF_YEAR] != day2[Calendar.DAY_OF_YEAR]
                || day1[Calendar.YEAR] != day2[Calendar.YEAR]
    }

    fun utcToDate(timestamp: Int): Date {
        return Date(timestamp.toLong()  * 1000)
    }

}