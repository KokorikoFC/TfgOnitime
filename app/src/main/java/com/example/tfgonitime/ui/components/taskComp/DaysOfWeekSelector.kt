package com.example.tfgonitime.ui.components.taskComp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.*


@Composable
fun DaysOfWeekSelector(
    daysOfWeekAbbreviations: List<String> = listOf("L", "M", "X", "J", "V", "S", "D"),
    selectedDaysFullNames: List<String>,
    onDaySelected: (String) -> Unit
) {
    val dayMap = remember {
        mapOf(
            "L" to "MONDAY",
            "M" to "TUESDAY",
            "X" to "WEDNESDAY",
            "J" to "THURSDAY",
            "V" to "FRIDAY",
            "S" to "SATURDAY",
            "D" to "SUNDAY"
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Brown, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {

        Text(
            text = stringResource(R.string.repeat_label),
            modifier = Modifier
                .fillMaxWidth()
                .background(Green)
                .padding(8.dp),
            color = White,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use abbreviations for display
            daysOfWeekAbbreviations.forEach { abbreviation ->
                val fullName = dayMap[abbreviation] ?: abbreviation

                DayChip(
                    text = abbreviation,
                    selected = selectedDaysFullNames.contains(fullName),
                    onClick = {
                        onDaySelected(fullName)
                    }
                )
            }
        }
    }
}


@Composable
fun DayChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .width(35.dp)
            .height(35.dp)
            .border(
                width = 1.dp,
                color =Brown,
                shape = CircleShape
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Brown else MaterialTheme.colorScheme.background,
            contentColor = White,
            disabledContainerColor = White,
            disabledContentColor = Brown
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Brown,
                modifier = Modifier.align(Alignment.Center)

            )
        }
    }

}