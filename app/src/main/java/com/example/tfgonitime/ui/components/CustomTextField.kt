package com.example.tfgonitime.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.example.tfgonitime.ui.theme.*

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, modifier = Modifier.padding(0.dp)) },
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Brown,
            focusedBorderColor = Brown,
            focusedLabelColor = Brown,
            unfocusedLabelColor = Brown,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        )
    )

}

