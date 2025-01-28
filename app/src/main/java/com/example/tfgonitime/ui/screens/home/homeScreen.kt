package com.example.tfgonitime.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.theme.*
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Parte superior (40% de la pantalla)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f) // 40% de la pantalla
                            .background(Brown),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Button(
                            onClick = {
                                navHostController.navigate("addTaskScreen")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = "Añadir Tarea")
                        }
                    }

                    // Parte inferior (60% de la pantalla) con scroll
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Green)
                    ) {
                        // LazyColumn para scroll en la parte verde
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp) // Padding dentro de la columna verde
                        ) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp)) // Esquinas redondeadas
                                        .background(White) // Fondo blanco
                                        .padding(20.dp), // Padding dentro del cuadro
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Encabezado "Categoría"
                                    Text(
                                        "Categoría",
                                        modifier = Modifier.fillMaxWidth(),
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp,
                                            color = Brown
                                        )
                                    )
                                    // Espaciado después del encabezado
                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Iteración sobre la lista de tareas (dentro del cuadro blanco)
                                    tasks.forEachIndexed { index, task ->
                                        TaskItem(
                                            task = task,
                                            navHostController = navHostController,
                                            userId = userId,
                                            onDelete = {
                                                taskViewModel.deleteTask(
                                                    userId,
                                                    task.id
                                                )
                                            },
                                            onEdit = {
                                                navHostController.navigate("editTaskScreen/${task.id}")
                                            },
                                            taskViewModel = taskViewModel,
                                            index = index,
                                            totalItems = tasks.size
                                        )
                                    }
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
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    taskViewModel: TaskViewModel,
    index: Int,
    totalItems: Int
) {
    var showPopup by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(task.completed) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .border(2.dp, DarkBrown)
            .clickable { showPopup = true }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(0.6f)
                    .clip(RoundedCornerShape(500.dp))
                    .background(Green)
            )

            Text(text = task.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (task.completed) "Completada" else "Pendiente",
                color = if (task.completed) Color.Green else Color.Red
            )

            Checkbox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    checked = isChecked
                    taskViewModel.updateTaskCompletion(userId, task.id, isChecked)
                }
            )
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

    // Divider entre tareas, excepto en la última
    if (index != totalItems - 1) {
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            modifier = Modifier
                .height(1.dp)
                .background(DarkBrown)
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}


