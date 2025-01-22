package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AddTaskScreen(navHostController: NavHostController,taskViewModel: TaskViewModel) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid


    // Estado para los campos del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Estado para mostrar un mensaje de éxito o error
    var message by remember { mutableStateOf("") }

    // Mensaje de éxito o error
    if (message.isNotEmpty()) {
        Text(
            text = message,
            color = if (message.startsWith("Error")) Color.Red else Color.Green,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Por favor inicia sesión para ver tus tareas.",
                color = Color.Red,
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()){
            // Formulario de entrada para la tarea
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título de la tarea") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción de la tarea") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        val task = Task(title = title, description = description, completed = false)
                        taskViewModel.addTask(userId, task)
                        message = "Tarea agregada exitosamente"
                        title = ""
                        description = ""
                    } else {
                        message = "Por favor, completa todos los campos"
                    }
                    navHostController.navigate("homeScreen")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Agregar Tarea")
            }
        }
    }
}