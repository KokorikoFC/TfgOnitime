package com.example.tfgonitime.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.homeComp.InteractiveHome
import com.example.tfgonitime.ui.components.taskComp.CustomFloatingButton
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

    val colorMap = mapOf(
        "LightRed" to LightRed,
        "LightOrange" to LightOrange,
        "Yellow" to Yellow,
        "LightGreen" to LightGreen,
        "LightBlue" to LightBlue,
        "LightPink" to LightPink,
        "Purple" to Purple,
        "LightPurple" to LightPurple,
        "LightBrown" to LightBrown
    )

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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Parte superior (40% de la pantalla)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.37f)
                                .background(White)
                                .zIndex(0f),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            InteractiveHome()

                            //boton tienda
                            IconButton(
                                onClick = { navHostController.navigate("storeScreen") },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (0).dp, y = (-5).dp)
                                    .size(55.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Ir tienda",
                                    tint = DarkBrown
                                )
                            }
                            //boton cambiar muebles
                            IconButton(
                                onClick = { navHostController.navigate("inventoryScreen") },
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .offset(x = (0).dp, y = (-5).dp)
                                    .size(55.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chair,
                                    contentDescription = "Cambiar muebles",
                                    tint = DarkBrown
                                )
                            }
                            //boton cambiar mascota
                            IconButton(
                                onClick = { navHostController.navigate("petCatalogueScreen") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (0).dp, y = (-5).dp)
                                    .size(55.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChangeCircle,
                                    contentDescription = "Cambiar mascota",
                                    tint = DarkBrown
                                )
                            }
                        }

                        // Parte inferior (70% de la pantalla) con scroll
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .zIndex(1f)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .background(Green.copy(alpha = 0.63f))
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(40.dp))
                                }

                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(White)
                                            .padding(20.dp)
                                    ) {
                                        Text(
                                            text = "General",
                                            modifier = Modifier.fillMaxWidth(),
                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 20.sp,
                                                color = DarkBrown
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Filtrar las tareas que corresponden a este grupo
                                        val tasksForGroup = tasks.filter { task ->
                                            task.groupId.isNullOrEmpty()
                                        }

                                        if (tasksForGroup.isNotEmpty()) {
                                            tasksForGroup.forEachIndexed { index, task ->
                                                TaskItem(
                                                    task = task,
                                                    userId = userId,
                                                    onDelete = {
                                                        taskViewModel.deleteTask(userId, task.id)
                                                    },
                                                    onEdit = {
                                                        navHostController.navigate("editTaskScreen/${task.id}")
                                                    },
                                                    taskViewModel = taskViewModel,
                                                    index = index,
                                                    totalItems = tasksForGroup.size,
                                                    color = DarkBrown
                                                )
                                            }
                                        } else {
                                            // Si no hay tareas para este grupo, mostramos un mensaje
                                            Text(
                                                text = "No hay tareas para este grupo.",
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    color = Color.Gray
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(30.dp))
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
                                            text = group.groupName,
                                            modifier = Modifier.fillMaxWidth(),
                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 20.sp,
                                                color = colorMap[group.groupColor]
                                                    ?: DarkBrown //Usar color del grupo
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
                                                    userId = userId,
                                                    onDelete = {
                                                        taskViewModel.deleteTask(userId, task.id)
                                                    },
                                                    onEdit = {
                                                        navHostController.navigate("editTaskScreen/${task.id}")
                                                    },
                                                    taskViewModel = taskViewModel,
                                                    index = index,
                                                    totalItems = tasksForGroup.size,
                                                    color = colorMap[group.groupColor] ?: DarkBrown
                                                )
                                            }
                                        } else {
                                            // Si no hay tareas para este grupo, mostramos un mensaje
                                            Text(
                                                text = "No hay tareas para este grupo.",
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    color = Color.Gray
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(30.dp))
                                }
                            }
                        }
                    }
                    CustomFloatingButton { navHostController.navigate("addTaskScreen") }
                }
            }
        )
    }
}


