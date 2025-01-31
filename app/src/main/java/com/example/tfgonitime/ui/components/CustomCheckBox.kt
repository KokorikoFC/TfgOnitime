package com.example.tfgonitime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Gray
import com.example.tfgonitime.ui.theme.White

@Composable
fun CustomCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedIcon: ImageVector = Icons.Default.Check,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(25.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(2.dp , DarkBrown, RoundedCornerShape(6.dp))
            .clickable { onCheckedChange(!checked) }
            .background(if (checked) Gray else White),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = checkedIcon,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
