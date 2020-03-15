package com.zhang.change.utils

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