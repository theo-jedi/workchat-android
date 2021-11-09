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

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val day1 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date1)
        val day2 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date2)
        return day1.equals(day2)
    }

    fun utcToDate(timestamp: Int): Date {
        return Date(timestamp.toLong()  * 1000)
    }

}