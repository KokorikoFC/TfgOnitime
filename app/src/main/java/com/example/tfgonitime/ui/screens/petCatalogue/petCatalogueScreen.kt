package com.example.tfgonitime.ui.screens.petCatalogue

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.remember
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel

@Composable
fun PetCatalogueScreen(navHostController: NavHostController) {
    val petsViewModel: PetsViewModel = viewModel()
    val petsList by petsViewModel.pets.collectAsState()
    val errorMessage by petsViewModel.errorMessage.collectAsState(null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
    ) {
        // Parte superior (45% de la pantalla)
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
                    Image(
                        painter = painterResource(R.drawable.head_daifuku),
                        contentDescription = "Mascota Decorativa",
                        modifier = Modifier
                            .size(80.dp)
                            .offset(y = 80.dp)
                    )
                    Image(
                        painter = painterResource(R.drawable.coffee_jelly_body_1),
                        contentDescription = "Mascota Decorativa",
                        modifier = Modifier
                            .size(150.dp)
                    )
                }
            }
        }

        // Parte inferior (área para la lista de mascotas)
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
                Text(text = "Error: $errorMessage", color = Color.Red)
            } else if (petsList.isEmpty()) {
                CircularProgressIndicator() // O algún indicador de carga
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(petsList) { pet ->
                        PetCard(pet = pet)
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(pet: Pets) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageId = getDrawableResourceId(pet.pose1)
            if (imageId != 0) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = "Imagen de ${pet.id}",
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Text("Imagen no encontrada", textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ID: ${pet.id}",
                style = TextStyle(fontSize = 14.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Función auxiliar para obtener el ID del recurso drawable por nombre
fun getDrawableResourceId(resourceName: String?): Int {
    if (resourceName.isNullOrEmpty()) {
        return 0
    }
    return try {
        R.drawable::class.java.getField(resourceName).getInt(null)
    } catch (e: Exception) {
        Log.e("PetCatalogueScreen", "Error al obtener el recurso drawable: ${e.message}")
        0
    }
}