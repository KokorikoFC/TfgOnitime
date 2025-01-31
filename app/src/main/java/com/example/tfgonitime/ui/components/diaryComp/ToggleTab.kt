package com.example.tfgonitime.ui.components.diaryComp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Gray
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel

@Composable
fun ToggleTab(record: MutableState<Boolean>, diaryViewModel: DiaryViewModel) {

    var isSelected by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Gray), // Fondo gris claro
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (!isSelected) Green else Color.Transparent)
                .clickable(
                    indication = null, // Eliminar indicación de clic
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    isSelected = false
                    record.value = false  // Establece record a true al hacer clic
                }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.toggle_tab_option_1),
                color = if (!isSelected) Color.White else Color.Black,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Green else Color.Transparent)
                .clickable(
                    indication = null, // Eliminar indicación de clic
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    isSelected = true
                    record.value = true  // Establece record a true al hacer clic
                    diaryViewModel.clearSelectedMood()
                } // Cambiar estado al hacer clic
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.toggle_tab_option_2),
                color = if (isSelected) Color.White else Color.Black,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

