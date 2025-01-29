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
import com.example.tfgonitime.ui.components.taskComp.TaskItem
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    taskViewModel: TaskViewModel,
    groupViewModel: GroupViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Estado para las tareas y los grupos
    val tasks by taskViewModel.tasksState.collectAsState()
    val groups by groupViewModel.groupsState.collectAsState()

    // Si el usuario no está autenticado, mostramos un mensaje
    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Por favor inicia sesión para ver tus tareas.", color = Color.Red)
        }
    } else {
        // Llamar al ViewModel para obtener las tareas y grupos
        LaunchedEffect(userId) {
            taskViewModel.loadTasks(userId)
            groupViewModel.loadGroups(userId)
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
                            .fillMaxHeight(0.4f)
                            .background(White),
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
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(Green)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }

                            items(groups) { group ->

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(White)
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        group.groupName,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp,
                                            color = Brown
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Filtrar las tareas que corresponden a este grupo
                                    val tasksForGroup = tasks.filter { task ->
                                        task.groupId == group.groupId
                                    }

                                    // Si hay tareas, las mostramos
                                    if (tasksForGroup.isNotEmpty()) {
                                        tasksForGroup.forEachIndexed { index, task ->
                                            TaskItem(
                                                task = task,
                                                navHostController = navHostController,
                                                userId = userId,
                                                onDelete = {
                                                    taskViewModel.deleteTask(userId, task.id)
                                                },
                                                onEdit = {
                                                    navHostController.navigate("editTaskScreen/${task.id}")
                                                },
                                                taskViewModel = taskViewModel,
                                                index = index,
                                                totalItems = tasksForGroup.size
                                            )
                                        }
                                    } else {
                                        // Si no hay tareas para este grupo, mostramos un mensaje
                                        Text(
                                            text = "No hay tareas para este grupo.",
                                            style = TextStyle(fontSize = 16.sp, color = Color.Gray)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(30.dp))
                            }
                        }
                    }
                }
            }
        )
    }
}


