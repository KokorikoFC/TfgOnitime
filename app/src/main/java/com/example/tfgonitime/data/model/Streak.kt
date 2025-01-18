package com.example.tfgonitime.data.model

import com.google.firebase.Timestamp

class Streak {
    data class Streak(
        val streakCount: Int = 0, // veces que se ha completado los 7 días de check in
        val lastLoginDate: Timestamp = Timestamp.now(),
        val checkInCount: Int = 0, // Días consecutivos con check-in
        val days: List<DayCheckIn> = List(7) { DayCheckIn(day = it + 1) }
    )

    data class DayCheckIn(
        val day: Int,
        var reward: String = "100 coins",
        var checkedToday: Boolean = false,
        var petId: String? = null
    )


}