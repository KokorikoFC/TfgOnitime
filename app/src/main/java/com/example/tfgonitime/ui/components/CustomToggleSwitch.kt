package com.example.tfgonitime.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import com.example.tfgonitime.ui.theme.*

@Composable
fun CustomToggleSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = { onCheckedChange(it) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = White,
            uncheckedThumbColor = Green,
            checkedTrackColor = Green,
            uncheckedTrackColor = White,
            uncheckedBorderColor = Green
        )
    )
}
