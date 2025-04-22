package com.example.tfgonitime.ui.screens.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.homeComp.InteractiveHome
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.FurnitureUiState
import com.example.tfgonitime.viewmodel.FurnitureViewModel

@Composable
fun InventoryScreen(navHostController: NavHostController, viewModel: FurnitureViewModel) {
    // Obtener el estado de los muebles desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Parte superior (40% de la pantalla)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.50f)
                .zIndex(0f),
            contentAlignment = Alignment.TopCenter
        ) {
            GoBackArrow(
                onClick = {
                    navHostController.navigate("homeScreen") {
                        popUpTo("homeScreen") { inclusive = true }
                    }
                },
                isBrown = true,
                title = "",
                modifier = Modifier.padding(start = 20.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp)) // Esto la empuja hacia abajo
                InteractiveHome()
            }
        }

        // Parte inferior (63% de la pantalla)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.50f)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Brown.copy())
        ) {
            when (uiState) {
                is FurnitureUiState.Loading -> {
                    // Mostrar el indicador de carga mientras se obtienen los muebles
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FurnitureUiState.Success -> {
                    // Mostrar los muebles cuando la carga haya sido exitosa
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                            .border(1.dp, White),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        val furnitureItems = (uiState as FurnitureUiState.Success).furnitureByTheme
                        // Extraer los muebles de cada tema
                        furnitureItems.flatMap { it.value }.let { items ->
                            items.forEach { furniture ->
                                item {
                                    InventoryCard(furniture = furniture)
                                }
                            }
                        }
                    }
                }
                is FurnitureUiState.Error -> {
                    // Mostrar el mensaje de error si hubo un problema
                    Text(
                        text = (uiState as FurnitureUiState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryCard(furniture: Furniture) {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(150.dp) // Ajustar el tamaño según el diseño
            .clip(RoundedCornerShape(10.dp))
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen del mueble
        Image(
            painter = painterResource(R.drawable.default_furniture), // Aquí puedes poner la imagen real del mueble
            contentDescription = furniture.name,
            modifier = Modifier
                .size(80.dp) // Ajustar el tamaño de la imagen
                .clickable {
                    // Acción al hacer clic en la imagen
                },
            contentScale = ContentScale.Crop
        )

        // Nombre del mueble debajo de la imagen
        Text(
            text = furniture.name, // Mostrar el nombre del mueble
            style = TextStyle(
                fontSize = 12.sp,
                color = DarkBrown
            ),
            modifier = Modifier.padding(top = 8.dp) // Añadir espacio entre la imagen y el nombre
        )
    }
}
