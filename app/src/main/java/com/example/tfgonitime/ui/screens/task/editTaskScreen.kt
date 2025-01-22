package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun EditTaskScreen(taskId: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Editando tarea con ID: ${taskId ?: "No ID"}",
        )

        // Aquí puedes agregar el formulario de edición
    }
}
