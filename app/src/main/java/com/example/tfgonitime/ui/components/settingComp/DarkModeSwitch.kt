package com.example.tfgonitime.ui.components.settingComp

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White

@Composable
fun DarkModeSwitch(
    isDarkTheme: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = isDarkTheme,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = White,
            uncheckedThumbColor = Green,
            checkedTrackColor = Green,
            uncheckedTrackColor = White,
            uncheckedBorderColor = Green
        )
    )
}
