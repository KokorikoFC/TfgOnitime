package com.example.tfgonitime.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navHostController: NavHostController, taskViewModel: TaskViewModel) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Estado para las tareas y el loading
    val tasks by taskViewModel.tasksState.collectAsState()
    val loading by taskViewModel.loadingState.collectAsState()

    // Estado para mostrar el mensaje de error o éxito
    var message by remember { mutableStateOf("") }

    // Si el usuario no está autenticado, mostramos un mensaje
    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Por favor inicia sesión para ver tus tareas.", color = Color.Red)
        }
    } else {
        // Llamar al ViewModel para obtener las tareas
        LaunchedEffect(userId) {
            taskViewModel.loadTasks(userId)
        }


        Scaffold(
            containerColor = Color.White,
            bottomBar = { CustomBottomNavBar(navHostController) },
            content = { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = {
                                navHostController.navigate("addTaskScreen")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Añadir Tarea")
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tareas de ${currentUser.displayName ?: "Usuario"}",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Mostrar la lista de tareas
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(tasks) { task ->
                                    TaskItem(
                                        task = task,
                                        navHostController = navHostController,
                                        userId = userId ?: "", // Asegúrate de que el `userId` esté bien inicializado
                                        onDelete = {
                                            taskViewModel.deleteTask(userId, task.id)  // Llamada a la función de eliminar
                                        },
                                        onEdit = {
                                            navHostController.navigate("editTaskScreen/${task.id}")  // Navegar a la pantalla de editar tarea
                                        },
                                        taskViewModel = taskViewModel
                                    )
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
fun TaskItem(
    task: Task,
    navHostController: NavHostController,
    userId: String,
    onDelete: () -> Unit,  // Función para eliminar tarea
    onEdit: () -> Unit,    // Función para editar tarea
    taskViewModel: TaskViewModel // El ViewModel para actualizar la tarea
) {
    var showPopup by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(task.completed) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showPopup = true } // Abre el popup al hacer clic
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {


                Text(text = task.title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        checked = isChecked
                        // Llama a la función en el ViewModel para actualizar el estado en Firestore
                        taskViewModel.updateTaskCompletion(userId, task.id, isChecked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = if (task.completed) "Completada" else "Pendiente", color = if (task.completed) Color.Green else Color.Red)
        }
    }

    // Popup de opciones
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text(text = "Opciones") },
            text = {
                Column {
                    TextButton(onClick = { onEdit(); showPopup = false }) {
                        Text("Editar")
                    }
                    TextButton(onClick = { onDelete(); showPopup = false }) {
                        Text("Eliminar")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}




