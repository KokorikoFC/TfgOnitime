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
import com.example.tfgonitime.viewmodel.FurnitureViewModel
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.example.tfgonitime.viewmodel.StoreFurnitureUiState
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

// Import necessary components for pet display
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel // Import your PetsViewModel
import com.example.tfgonitime.viewmodel.AuthViewModel // Import your AuthViewModel
import com.example.tfgonitime.data.repository.PetsRepository // Import if needed to create ViewModel
import com.example.tfgonitime.data.repository.UserRepository // Import if needed to create ViewModel
import com.example.tfgonitime.data.model.Pets // Import the Pets data class
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    taskViewModel: TaskViewModel,
    groupViewModel: GroupViewModel,
    furnitureViewModel: FurnitureViewModel
) {
    // --- Obtain User ID and AuthViewModel ---
    // Getting user ID directly from FirebaseAuth
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Obtain AuthViewModel (needed for PetsViewModel dependency)
    val authViewModel: AuthViewModel = viewModel()

    // --- Obtain PetsViewModel ---
    // Get repositories - use remember to keep instances across recompositions
    val petsRepository = remember { PetsRepository() }
    val userRepository = remember { UserRepository() }

    // Obtain PetsViewModel, passing the dependencies
    // Using the inline ViewModelProvider.Factory
    val petsViewModel: PetsViewModel = viewModel(
        factory = remember { // Use remember for the factory instance
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PetsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        // Pass AuthViewModel and repositories to PetsViewModel constructor
                        return PetsViewModel(authViewModel, petsRepository, userRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
        // If you are using a DI framework (like Hilt or Koin), you would get the ViewModel differently
        // e.g., val petsViewModel: PetsViewModel = hiltViewModel()
    )

    // --- Observe necessary states ---
    // State for tasks and groups
    val tasks by taskViewModel.tasksState.collectAsState()
    val groups by groupViewModel.groupsState.collectAsState()

    // State for furniture
    val selectedFurnitureMap by furnitureViewModel.selectedFurnitureMap.collectAsState()
    val furnitureCatalog = (furnitureViewModel.storeUiState.value as? StoreFurnitureUiState.Success)?.furnitureList
        ?.flatMap { it.value } ?: emptyList()

    // State for pets (newly observed)
    val currentUserPetId by petsViewModel.currentUserPetId.collectAsState()
    val petsList by petsViewModel.pets.collectAsState()


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

    // If the user is not authenticated, show a message
    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize().wrapContentSize(Alignment.Center)) {
            Text(text = "Por favor inicia sesión para ver tus tareas.", color = Color.Red)
        }
    } else {
        // Call ViewModel to load tasks and groups when userId is available
        LaunchedEffect(userId) {
            taskViewModel.loadTasks(userId)
            groupViewModel.loadGroups(userId)
            furnitureViewModel.loadSelectedFurniture(userId)
            // PetsViewModel's loadUserPetId and fetchPets are called in its init block
            // and observe authViewModel.userId, so no explicit call needed here.
        }

        // --- Logic to determine the pet image to display ---
        // Find the selected pet from the list based on its ID
        // Use remember to avoid recalculating this unless petsList or currentUserPetId changes
        val selectedPet: Pets? = remember(petsList, currentUserPetId) {
            petsList.find { it.id == currentUserPetId }
        }

        // Determine the image resource ID for the selected pet's pose1
        // Use remember to avoid recalculating this unless selectedPet or context changes
        val context = LocalContext.current
        val selectedPetImageResId: Int? = remember(selectedPet, context) {
            selectedPet?.pose1?.let { imageName ->
                // Use the imageName (e.g., "taiyaki_body_1") to get the drawable resource ID
                context.resources.getIdentifier(imageName, "drawable", context.packageName)
                    .takeIf { it != 0 } // Ensure the resource exists and ID is not 0
            }
            // Returns null if selectedPet is null, pose1 is null/empty, or resource not found
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
                        // Top part (45% of the screen)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.45f)
                                .background(White)
                                .zIndex(0f),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            // --- Call InteractiveHome with the dynamic pet image ---
                            InteractiveHome(
                                selectedPetImageResId = selectedPetImageResId, // Pass the dynamic pet image ID
                                selectedFurnitureMap = selectedFurnitureMap, // Pass your existing furniture map
                                furnitureCatalog = furnitureCatalog // Pass your existing furniture catalog
                            )

                            // Shop button
                            IconButton(
                                onClick = { navHostController.navigate("storeScreen") },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (0).dp, y = (-5).dp) // Adjust offset if needed
                                    .size(55.dp) // Adjust size if needed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Ir tienda",
                                    tint = DarkBrown
                                )
                            }
                            // Change furniture button
                            IconButton(
                                onClick = { navHostController.navigate("inventoryScreen") },
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .offset(x = (0).dp, y = (-5).dp) // Adjust offset if needed
                                    .size(55.dp) // Adjust size if needed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chair,
                                    contentDescription = "Cambiar muebles",
                                    tint = DarkBrown
                                )
                            }
                            // Change pet button
                            IconButton(
                                onClick = { navHostController.navigate("petCatalogueScreen") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (0).dp, y = (-5).dp) // Adjust offset if needed
                                    .size(55.dp) // Adjust size if needed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChangeCircle,
                                    contentDescription = "Cambiar mascota",
                                    tint = DarkBrown
                                )
                            }
                        }

                        // Bottom part (55% of the screen) with scroll
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

                                        // Filter tasks that belong to this group (or no group)
                                        val tasksForGroup = tasks.filter { task ->
                                            task.groupId.isNullOrEmpty()
                                        }

                                        if (tasksForGroup.isNotEmpty()) {
                                            tasksForGroup.forEachIndexed { index, task ->
                                                TaskItem(
                                                    task = task,
                                                    userId = userId, // Pass userId to TaskItem if needed
                                                    onDelete = {
                                                        taskViewModel.deleteTask(userId!!, task.id) // Use !! after checking null
                                                    },
                                                    onEdit = {
                                                        navHostController.navigate("editTaskScreen/${task.id}")
                                                    },
                                                    taskViewModel = taskViewModel,
                                                    index = index,
                                                    totalItems = tasksForGroup.size,
                                                    color = DarkBrown // Default color for General
                                                )
                                            }
                                        } else {
                                            // If there are no tasks for this group, show a message
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
                                                    ?: DarkBrown //Use group color, fallback to DarkBrown
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
                                                    userId = userId, // Pass userId to TaskItem if needed
                                                    onDelete = {
                                                        taskViewModel.deleteTask(userId!!, task.id) // Use !! after checking null
                                                    },
                                                    onEdit = {
                                                        navHostController.navigate("editTaskScreen/${task.id}")
                                                    },
                                                    taskViewModel = taskViewModel,
                                                    index = index,
                                                    totalItems = tasksForGroup.size,
                                                    color = colorMap[group.groupColor] ?: DarkBrown // Pass group color, fallback
                                                )
                                            }
                                        } else {
                                            // If there are no tasks for this group, show a message
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
                    // Floating Action Button
                    CustomFloatingButton { navHostController.navigate("addTaskScreen") }
                }
            }
        )
    }
}