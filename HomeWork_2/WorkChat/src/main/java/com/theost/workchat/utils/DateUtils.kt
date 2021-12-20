package com.theost.workchat.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object DateUtils {

    fun getDayDate(date: Date): String {
        return SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }

    fun getTime(date: Date) : String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val day1 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date1)
        val day2 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date2)
        return day1.equals(day2)
    }

    fun getRandomDateBefore(date: Date = Date()): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        val randomDay = (1..currentDay).random()
        val randomHour = (0..23).random()
        val randomMinute = (0..59).random()
        calendar.set(Calendar.DAY_OF_YEAR, randomDay)
        calendar.set(Calendar.HOUR_OF_DAY, randomHour)
        calendar.set(Calendar.MINUTE, randomMinute)
        return calendar.time
    }

}