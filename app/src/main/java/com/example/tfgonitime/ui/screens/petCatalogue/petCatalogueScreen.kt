package com.example.tfgonitime.ui.screens.petCatalogue

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel
import com.example.tfgonitime.ui.theme.Beige
import com.example.tfgonitime.ui.theme.DarkBrown

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
fun PetCard(pet: Pets, onClick: () -> Unit = {}) {
    val context = LocalContext.current
    val imageName = pet.pose1 // Asumimos que pose1 es el nombre de la imagen

    val imageResId = remember(imageName) {
        val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        if (resId == 0) R.drawable.takoyaki_body_1
        else resId
    }

    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .height(175.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Beige)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Si la imagen existe, la cargamos, sino mostramos un mensaje
        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen de ${pet.id}",
                modifier = Modifier.size(110.dp)
            )
        } else {
            Text("Imagen no encontrada", color = DarkBrown)
        }

        Text(
            text = pet.id.takeIf { it.isNotBlank() } ?: "Mascota sin ID",
            style = TextStyle(
                fontSize = 12.sp, // Tamaño de fuente similar a InventoryCard
                color = DarkBrown, // Color de texto similar a InventoryCard
                fontWeight = FontWeight.Bold // Peso de la fuente similar a InventoryCard
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
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