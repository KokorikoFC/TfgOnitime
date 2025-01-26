package com.example.tfgonitime.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.repository.LanguageManager
import com.example.tfgonitime.ui.components.CustomRadioButton
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun HomeScreen(navHostController: NavHostController, taskViewModel: TaskViewModel) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Estado para los campos del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Estado para mostrar un mensaje de éxito o error
    var message by remember { mutableStateOf("") }

    // Si el usuario no está autenticado, mostramos un mensaje
    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Por favor inicia sesión para ver tus tareas.",
                color = Color.Red,
            )
        }
    } else {
        // Obtener las tareas desde el ViewModel
        val tasks by taskViewModel.tasksState.collectAsState()
        val loading by taskViewModel.loadingState.collectAsState()

        // Llamar al ViewModel para obtener las tareas
        LaunchedEffect(userId) {
            taskViewModel.loadTasks(userId)  // Pasamos el userId aquí
        }

        Scaffold(
            containerColor = Color.White,
            bottomBar = { CustomBottomNavBar(navHostController) },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = {
                                navHostController.navigate("addTaskScreen")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Ir a tarea")
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            // Título de la pantalla
                            Text(
                                text = "Tareas de ${currentUser.displayName ?: "Usuario"}",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )




                            Spacer(modifier = Modifier.height(16.dp))

                            // Mostrar la lista de tareas
                            LazyColumn {
                                items(tasks) { task ->
                                    TaskItem(task, navHostController)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun TaskItem(task: Task, navHostController: NavHostController) {
    // Este es un contenedor para cada tarea, puede contener un título, descripción y otros elementos
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp).clickable(
                onClick = {
                }
            ).clickable(
                onClick = {
                    navHostController.navigate("editTaskScreen/${task}")
                }
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título de la tarea
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Descripción de la tarea
            Text(
                text = task.description,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Muestra si la tarea está completada o no
            Text(
                text = if (task.completed) "Completada" else "Pendiente",
                color = if (task.completed) Color.Green else Color.Red
            )
        }
    }
}



