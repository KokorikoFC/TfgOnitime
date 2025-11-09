package com.example.tfgonitime.notification

import java.util.Calendar
import java.util.Locale

fun String.toCalendarDay(): Int? {
    return when (this.uppercase(Locale.getDefault())) {
        "MONDAY" -> Calendar.MONDAY
        "TUESDAY" -> Calendar.TUESDAY
        "WEDNESDAY" -> Calendar.WEDNESDAY
        "THURSDAY" -> Calendar.THURSDAY
        "FRIDAY" -> Calendar.FRIDAY
        "SATURDAY" -> Calendar.SATURDAY
        "SUNDAY" -> Calendar.SUNDAY
        else -> null
    }
}