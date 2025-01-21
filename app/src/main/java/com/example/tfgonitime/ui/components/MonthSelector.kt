package com.example.tfgonitime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.viewmodel.DiaryViewModel
import java.time.YearMonth

@Composable
fun MonthSelector(
    userId: String,
    currentMonth: MutableState<YearMonth>,
    diaryViewModel: DiaryViewModel,
    onMonthChange: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            val newMonth = currentMonth.value.minusMonths(1)
            currentMonth.value = newMonth
            onMonthChange(newMonth)
            diaryViewModel.loadMoods(userId, newMonth.year.toString(), newMonth.monthValue.toString().padStart(2, '0'))
        }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Mes anterior",
                tint = Color.Black
            )
        }

        Text(
            text = "${currentMonth.value.year}.${currentMonth.value.monthValue.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        IconButton(onClick = {
            val newMonth = currentMonth.value.plusMonths(1)
            currentMonth.value = newMonth
            onMonthChange(newMonth)
            diaryViewModel.loadMoods(userId, newMonth.year.toString(), newMonth.monthValue.toString().padStart(2, '0'))
        }) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Mes siguiente",
                tint = Color.Black
            )
        }
    }
}
