package com.example.tfgonitime.ui.screens.petCatalogue

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Pets
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel
import com.example.tfgonitime.presentation.viewmodel.AllPetsUiState
import com.example.tfgonitime.presentation.viewmodel.UserPetUiState
import com.example.tfgonitime.ui.theme.Beige
import com.example.tfgonitime.ui.theme.DarkBrown


@Composable
fun PetCatalogueScreen(
    navHostController: NavHostController,
    petsViewModel: PetsViewModel
) {
    val userPetUiState by petsViewModel.userPetUiState.collectAsState()
    val allPetsUiState by petsViewModel.allPetsUiState.collectAsState()

    LaunchedEffect(Unit) {
        petsViewModel.loadAllPets()
        petsViewModel.loadUserPet()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
    ) {

        // Parte superior (área de la mascota seleccionada, 45% de la pantalla)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .zIndex(1f), // Aseguramos que la parte superior esté encima
            contentAlignment = Alignment.TopCenter
        ) {
            // Botón para volver a la pantalla principal
            GoBackArrow(
                onClick = {
                    navHostController.navigate("homeScreen") {
                        popUpTo("homeScreen") { inclusive = true }
                    }
                },
                isBrown = false,
                title = "Mascotas",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp)
            )
            // Imagen del plato
            Image(
                painter = painterResource(R.drawable.plate),
                contentDescription = "Plato",
                modifier = Modifier
                    .size(250.dp)
                    .offset(y = 100.dp)
                    .zIndex(0f) // Plato detrás de la mascota y el indicador de carga
                    .align(Alignment.Center)
            )


            // Área para mostrar la mascota seleccionada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 60.dp) // Espacio para el botón y título superior
            ) {
                Box() {
                    when (userPetUiState) {
                        is UserPetUiState.Loading -> {
                            // Mostrar indicador de carga encima de la mascota
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = White)
                            }
                        }
                        is UserPetUiState.Success -> {
                            val selectedPet = (userPetUiState as UserPetUiState.Success).selectedPet

                            if (selectedPet != null) {
                                val imageName = selectedPet.pose2
                                val context = LocalContext.current
                                val imageResId = remember(selectedPet.id) {
                                    if (!imageName.isNullOrEmpty()) {
                                        context.resources.getIdentifier(imageName, "drawable", context.packageName)
                                            .takeIf { it != 0 } ?: R.drawable.default_pet
                                    } else {
                                        R.drawable.default_pet
                                    }
                                }

                                // Mostrar la mascota seleccionada encima del plato
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = imageResId),
                                        contentDescription = "Mascota Actual: ${selectedPet.name}",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .zIndex(2f) // Mascota encima del plato y el indicador
                                            .align(Alignment.Center)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No tienes mascota seleccionada",
                                        color = White,
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(fontSize = 18.sp)
                                    )
                                }
                            }
                        }
                        is UserPetUiState.Error -> {
                            val message = (userPetUiState as UserPetUiState.Error).message
                            Text(text = "Error cargando mascota: $message", color = Color.Red)
                        }
                        is UserPetUiState.NotLoggedIn -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Inicia sesión para ver tu mascota.",
                                    color = White,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontSize = 18.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Parte inferior (área para la lista de mascotas, 55% de la pantalla)
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
            when (allPetsUiState) {
                is AllPetsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AllPetsUiState.Success -> {
                    val petsList = (allPetsUiState as AllPetsUiState.Success).petsList
                    if (petsList.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(petsList) { pet ->
                                PetCard(
                                    pet = pet,
                                    onPetSelected = { petId ->
                                        petsViewModel.updateUserPetSelection(petId)
                                    }
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No se encontraron mascotas.", color = DarkBrown)
                        }
                    }
                }
                is AllPetsUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No hay mascotas disponibles en el catálogo.", color = DarkBrown, textAlign = TextAlign.Center)
                    }
                }
                is AllPetsUiState.Error -> {
                    val message = (allPetsUiState as AllPetsUiState.Error).message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error cargando catálogo: $message", color = Color.Red, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}





@Composable
fun PetCard(pet: Pets, onPetSelected: (String) -> Unit) {
    val context = LocalContext.current
    val imageName = pet.pose1

    val imageResId = remember(pet.id) {
        if (!imageName.isNullOrEmpty()) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
                .takeIf { it != 0 } ?: R.drawable.default_pet
        } else {
            R.drawable.default_pet
        }
    }

    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .height(175.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Beige)
            .clickable { onPetSelected(pet.id) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen de ${pet.name.takeIf { it.isNotBlank() } ?: pet.id}",
                modifier = Modifier.size(110.dp)
            )
        } else {
            Text("Imagen no encontrada", color = DarkBrown)
        }

        Text(
            text = pet.name.takeIf { it.isNotBlank() } ?: pet.id.takeIf { it.isNotBlank() } ?: "Mascota sin nombre", // Nombre o ID
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