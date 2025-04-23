package com.example.tfgonitime.ui.screens.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Importar items para LazyVerticalGrid
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
// Importar el estado de UI correcto para el inventario
import com.example.tfgonitime.viewmodel.UserInventoryUiState // Usar UserInventoryUiState
import com.example.tfgonitime.viewmodel.FurnitureViewModel
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.ui.platform.LocalContext
import com.example.tfgonitime.ui.components.inventory.InventoryCard


@Composable
fun InventoryScreen(navHostController: NavHostController, viewModel: FurnitureViewModel) {
    // Observar el estado correcto del inventario del usuario
    val inventoryUiState by viewModel.inventoryUiState.collectAsState()

    // Disparar la carga del inventario cuando la pantalla se crea
    LaunchedEffect(viewModel) {
        viewModel.loadUserInventory()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Parte superior (50% de la pantalla)
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

        // Parte inferior (área para la lista de inventario)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.50f)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Brown)
        ) {
            when (inventoryUiState) { // Usar el estado correcto del inventario (UserInventoryUiState)
                is UserInventoryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = White
                    )
                }
                is UserInventoryUiState.Success -> {
                    // Acceder a la lista de muebles directamente desde el estado Success
                    val ownedFurnitureList = (inventoryUiState as UserInventoryUiState.Success).ownedFurniture

                    // Usar LazyVerticalGrid para mostrar los muebles en una cuadrícula
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Usar gridItems con la lista de muebles del inventario
                        gridItems(ownedFurnitureList) { furniture ->

                            InventoryCard(
                                furniture = furniture,
                                onClick = {
                                    // Aquí puedes manejar lo que sucede al hacer clic
                                    // Por ejemplo, pasar el mueble seleccionado para colocarlo en la habitación
                                }
                            )
                        }
                    }
                }
                is UserInventoryUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tu inventario está vacío.\nVisita la tienda para adquirir muebles.",
                            color = White,
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                }
                is UserInventoryUiState.NotLoggedIn -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Inicia sesión para ver tu inventario.",
                            color = White,
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                }
                is UserInventoryUiState.Error -> {
                    // Mostrar el mensaje de error
                    Text(
                        text = "Error al cargar inventario: ${(inventoryUiState as UserInventoryUiState.Error).message}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

