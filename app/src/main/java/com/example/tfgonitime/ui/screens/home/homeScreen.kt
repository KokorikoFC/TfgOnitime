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
import androidx.compose.runtime.* // Ensure all runtime components are imported
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.homeComp.InteractiveHome
import com.example.tfgonitime.ui.components.taskComp.CustomFloatingButton
import com.example.tfgonitime.ui.components.taskComp.TaskItem
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.FurnitureViewModel
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.example.tfgonitime.viewmodel.StoreFurnitureUiState
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel
import com.example.tfgonitime.presentation.viewmodel.UserPetUiState

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    taskViewModel: TaskViewModel,
    groupViewModel: GroupViewModel,
    furnitureViewModel: FurnitureViewModel,
    petsViewModel: PetsViewModel
) {
    // --- Obtener ID de usuario y AuthViewModel ---
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Observamos el estado de la mascota seleccionada
    val userPetState by petsViewModel.userPetUiState.collectAsState()

    // Extraemos la mascota seleccionada
    val selectedPet = when (userPetState) {
        is UserPetUiState.Success -> (userPetState as UserPetUiState.Success).selectedPet
        else -> null
    }

    // Si hay una mascota seleccionada, obtenemos su nombre de imagen
    val selectedPetImageName = selectedPet?.pose1 // Suponiendo que pose1 es el nombre de la imagen

    // --- Observar estados necesarios ---
    // Estado para tareas y grupos
    val tasks by taskViewModel.tasksState.collectAsState()
    val groups by groupViewModel.groupsState.collectAsState()

    // Estado para muebles
    val selectedFurnitureMap by furnitureViewModel.selectedFurnitureMap.collectAsState()
    val furnitureCatalog = (furnitureViewModel.storeUiState.value as? StoreFurnitureUiState.Success)?.furnitureList
        ?.flatMap { it.value } ?: emptyList()

    // Mapa de colores para grupos
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

    // Si el usuario no está autenticado, muestra mensaje
    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize().wrapContentSize(Alignment.Center)) {
            Text(text = "Por favor inicia sesión para ver tus tareas.", color = Color.Red)
        }
    } else {
        // Cargar tareas y grupos cuando el userId está disponible
        LaunchedEffect(userId) {
            taskViewModel.loadTasks(userId)
            groupViewModel.loadGroups(userId)
            furnitureViewModel.loadSelectedFurniture(userId)
            petsViewModel.loadUserPet()
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
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
                        // Parte superior (45% de la pantalla)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.45f)
                                .zIndex(0f),
                            contentAlignment = Alignment.TopCenter
                        ) {

                            InteractiveHome(
                                showPet = true,
                                selectedFurnitureMap = selectedFurnitureMap,
                                furnitureCatalog = furnitureCatalog,
                                selectedPetImageResId = selectedPetImageName
                            )

                            // Botón de tienda
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

                            // Botón para cambiar muebles
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

                            // Botón para cambiar mascota
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

                        // Parte inferior (55% de la pantalla) con scroll
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
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.background)
                                            .padding(20.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.general),
                                            modifier = Modifier.fillMaxWidth(),
                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 20.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Filter tasks that belong to this group (or no group)
                                        val tasksForGroup = tasks.filter { task ->
                                            task.groupId.isNullOrEmpty()
                                        }

                                        if (tasksForGroup.isNotEmpty()) {
                                            tasksForGroup.forEachIndexed { index, task ->
                                                TaskItem(
                                                    task = task,
                                                    userId = userId,
                                                    onDelete = {
                                                        taskViewModel.deleteTask(userId!!, task.id)
                                                    },
                                                    onEdit = {
                                                        navHostController.navigate("editTaskScreen/${task.id}")
                                                    },
                                                    taskViewModel = taskViewModel,
                                                    index = index,
                                                    totalItems = tasksForGroup.size,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        } else {

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
                                            .background(MaterialTheme.colorScheme.background)
                                            .padding(20.dp)
                                    ) {
                                        Text(
                                            text = group.groupName,
                                            modifier = Modifier.fillMaxWidth(),
                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 20.sp,
                                                color = colorMap[group.groupColor]
                                                    ?: DarkBrown
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Filter tasks that belong to this group
                                        val tasksForGroup = tasks.filter { task ->
                                            task.groupId == group.groupId
                                        }

                                        // If there are tasks, show them
                                        if (tasksForGroup.isNotEmpty()) {
                                            tasksForGroup.forEachIndexed { index, task ->
                                                TaskItem(
                                                    task = task,
                                                    userId = userId,
                                                    onDelete = {
                                                        taskViewModel.deleteTask(userId!!, task.id)
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

                                item{
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }
                    }

                    // Floating Action Button
                    CustomFloatingButton { navHostController.navigate("addTaskScreen") }
                }
            }
        )
    }
}
