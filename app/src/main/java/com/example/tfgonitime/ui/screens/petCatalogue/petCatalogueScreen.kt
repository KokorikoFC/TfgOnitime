package com.example.tfgonitime.ui.screens.petCatalogue

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Pets
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel // Import the modified ViewModel

// Import your AuthViewModel
import com.example.tfgonitime.viewmodel.AuthViewModel

import com.example.tfgonitime.data.repository.PetsRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.example.tfgonitime.ui.theme.Beige
import com.example.tfgonitime.ui.theme.DarkBrown


@Composable
fun PetCatalogueScreen(
    navHostController: NavHostController,
    // You might receive AuthViewModel here if it's provided at a higher level,
    // or you can obtain it using viewModel() here as well.
    // Assuming you can obtain AuthViewModel here:
    authViewModel: AuthViewModel = viewModel() // Obtain AuthViewModel
) {
    // Obtain the repositories
    val petsRepository = remember { PetsRepository() }
    val userRepository = remember { UserRepository() }

    // Obtain the PetsViewModel, passing the dependencies
    // AndroidX's viewModel() will attempt to use a default factory if dependencies
    // are other ViewModels or standard injectable types.
    val petsViewModel: PetsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PetsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    // Pass AuthViewModel and repositories
                    return PetsViewModel(authViewModel, petsRepository, userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )


    // Observe the ViewModel states
    val petsList by petsViewModel.pets.collectAsState()
    val errorMessage by petsViewModel.errorMessage.collectAsState(null)
    val currentPetId by petsViewModel.currentUserPetId.collectAsState()

    // Observe the authentication state to potentially show a login message
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUserId by authViewModel.userId.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
    ) {
        // Part top (45% of the screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .zIndex(0f),
            contentAlignment = Alignment.TopCenter
        ) {
            GoBackArrow(
                onClick = {
                    navHostController.navigate("homeScreen") {
                        popUpTo("homeScreen") { inclusive = true }
                    }
                },
                isBrown = false,
                title = "Mascotas",
                modifier = Modifier.padding(start = 20.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Decorative head always shown
                    Image(
                        painter = painterResource(R.drawable.head_daifuku),
                        contentDescription = "Mascota Decorativa",
                        modifier = Modifier
                            .size(80.dp)
                            .offset(y = 80.dp) // Adjust offset as needed
                    )

                    // Display the current pet body if its ID is known and user is authenticated
                    if (isAuthenticated == true && currentPetId != null) {
                        // Buscar la mascota en la lista por su ID para obtener el nombre de la imagen
                        val currentPet = petsList.find { it.id == currentPetId }
                        val imageName = currentPet?.pose1 // Assuming pose1 is the body image
                        val context = LocalContext.current
                        // Use remember to avoid recalculating resId on every recomposition
                        val imageResId = remember(imageName) {
                            if (!imageName.isNullOrEmpty()) {
                                context.resources.getIdentifier(imageName, "drawable", context.packageName)
                                    .takeIf { it != 0 } ?: R.drawable.coffee_jelly_body_1 // Fallback if resource not found
                            } else {
                                R.drawable.coffee_jelly_body_1 // Fallback for null or empty imageName
                            }
                        }
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = "Mascota Actual",
                            modifier = Modifier.size(150.dp) // Adjust size as needed
                        )
                    } else if (isAuthenticated == false) {
                        // Show a placeholder or message if user is not authenticated
                        Image(
                            painter = painterResource(R.drawable.coffee_jelly_body_1), // Default or placeholder image
                            contentDescription = "Usuario no autenticado",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                    else {
                        // Show loading or default pet while authentication state is unknown or pet ID is loading
                        Image(
                            painter = painterResource(R.drawable.coffee_jelly_body_1), // Default or placeholder image
                            contentDescription = "Cargando mascota...",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }
            }
        }

        // Bottom part (area for the list of pets)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(White)
                .padding(16.dp)
        ) {
            if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: $errorMessage", color = Color.Red, textAlign = TextAlign.Center)
                }
            } else if (petsList.isEmpty()) {
                // Show loading only if no error and list is empty
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(petsList) { pet ->
                        // Only allow selection if the user is authenticated
                        val isUserAuthenticated = isAuthenticated ?: false // Treat null as false
                        PetCard(
                            pet = pet,
                            onPetSelected = if (isUserAuthenticated) { petId ->
                                petsViewModel.updateUserPetId(petId)
                            } else {
                                { /* Do nothing if not authenticated */ }
                            }
                        )
                    }
                }
            }
        }

        // Optional: Show a message if the user is not authenticated
        if (isAuthenticated == false) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent overlay
                    .clickable { /* Intercept clicks */ } // Consume clicks below
                , contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please log in to select a pet.",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// PetCard composable remains the same
@Composable
fun PetCard(pet: Pets, onPetSelected: (String) -> Unit) {
    val context = LocalContext.current
    val imageName = pet.pose1 // Assuming pose1 is the image you want to show in the card

    val imageResId = remember(imageName) {
        if (!imageName.isNullOrEmpty()) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
                .takeIf { it != 0 } ?: R.drawable.takoyaki_body_1 // Fallback image if resource not found
        } else {
            R.drawable.takoyaki_body_1 // Fallback for null or empty imageName
        }
    }

    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .height(175.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Beige)
            .clickable { onPetSelected(pet.id) } // Pass the ID when clicked
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen de ${pet.name.takeIf { it.isNotBlank() } ?: pet.id}",
                modifier = Modifier.size(110.dp) // Adjust size as needed
            )
        } else {
            Text("Imagen no encontrada", color = DarkBrown)
        }

        Text(
            text = pet.name.takeIf { it.isNotBlank() } ?: pet.id.takeIf { it.isNotBlank() } ?: "Mascota sin nombre",
            style = TextStyle(
                fontSize = 12.sp,
                color = DarkBrown,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}