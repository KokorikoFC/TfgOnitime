package com.example.tfgonitime.ui.components.diaryComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.viewmodel.DiaryViewModel
import java.time.YearMonth

@Composable
fun MonthSelector(
    userId: String,
    currentMonth: MutableState<YearMonth>,
    diaryViewModel: DiaryViewModel,
    onMonthChange: (YearMonth) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp) // Ajusta el tamaño si es necesario
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    val newMonth = currentMonth.value.minusMonths(1)
                    currentMonth.value = newMonth
                    onMonthChange(newMonth)
                    diaryViewModel.loadMoods(
                        userId,
                        newMonth.year.toString(),
                        newMonth.monthValue
                            .toString()
                            .padStart(2, '0')
                    )
                    diaryViewModel.clearSelectedMood()
                },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Mes anterior",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )

        }

        Text(
            text = "${
                currentMonth.value.monthValue.toString().padStart(2, '0')
            }/${currentMonth.value.year}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        Box(
            modifier = Modifier
                .size(48.dp) // Ajusta el tamaño si es necesario
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    val newMonth = currentMonth.value.plusMonths(1)
                    currentMonth.value = newMonth
                    onMonthChange(newMonth)
                    diaryViewModel.loadMoods(
                        userId,
                        newMonth.year.toString(),
                        newMonth.monthValue
                            .toString()
                            .padStart(2, '0')
                    )
                    diaryViewModel.clearSelectedMood()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Mes siguiente",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
