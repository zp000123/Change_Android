package com.zhang.change.utils

import java.math.BigDecimal
import java.util.*

fun Long.getMinDateStampDay(): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMinDateStampDay }
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
    calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))
    return calendar.timeInMillis
}

fun Long.getMaxDateStampDay(): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMaxDateStampDay }
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND))
    calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND))

    return calendar.timeInMillis
}


fun Long.getMinDateStampMonth(): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMinDateStampMonth }

    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
    calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))
    return calendar.timeInMillis
}

fun Long.getMaxDateStampMonth(): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMaxDateStampMonth }

    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND))
    calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND))

    return calendar.timeInMillis
}

fun Long.getMinDayInMonth(): Int {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMinDayInMonth }
    return calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
}

fun Long.getMaxDayInMonth(): Int {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMaxDayInMonth }
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun Long.isCurrMonth(): Boolean {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@isCurrMonth }
    val newCalendar = Calendar.getInstance()
    return calendar.get(Calendar.MONTH) == newCalendar.get(Calendar.MONTH) &&
            calendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR)
}

fun Long.getDayInMonth(): Int {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getDayInMonth }
    return calendar.get(Calendar.DAY_OF_MONTH)
}

fun Int.getNiceStr(): String {
    return when {
        this == 0 -> ""
        this % 100 == 0 -> {
            "${this / 100}"
        }
        else -> {
            "${this / 100.0}"
        }
    }
}


fun Long.getNiceStr(): String {
    return when {
        this == 0L -> ""
        this % 100 == 0L -> {
            "${this / 100}"
        }
        else -> {
            "${this / 100.0}"
        }
    }
}

val BigDecimal_100 = BigDecimal(100)