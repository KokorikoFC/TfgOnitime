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
import com.example.tfgonitime.ui.theme.*

@Composable
fun GenderSelectableChip(
    gender: String,
    selectedGender: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val isSelected = selectedGender == gender

    FilterChip(
        selected = isSelected,
        onClick = { onSelect(gender) },
        label = {
            Text(
                text = gender,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp) // Padding interior
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
