package com.example.tfgonitime.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.tfgonitime.ui.theme.*

@Composable
fun GenderSelectableChip(
    displayText: String,
    internalValue: String,
    selectedGender: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedGender == internalValue

    FilterChip(
        selected = isSelected,
        onClick = { onSelect(internalValue) }, // Envía el valor interno (siempre en español)
        label = {
            Text(
                text = displayText, // Muestra el texto visible para el usuario
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier.fillMaxWidth(),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Green,
            selectedLabelColor = White,
            containerColor = White,
            labelColor = DarkBrown
        ),
        border = BorderStroke(2.dp, DarkBrown)
    )
}

