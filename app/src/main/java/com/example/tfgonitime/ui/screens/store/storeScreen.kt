package com.example.tfgonitime.ui.screens.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.storeComp.FurnitureCard
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White
// **Importar el estado de UI correcto para la tienda**
import com.example.tfgonitime.viewmodel.StoreFurnitureUiState
import com.example.tfgonitime.viewmodel.FurnitureViewModel
import com.example.tfgonitime.viewmodel.UserInventoryUiState
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.grid.items as gridItems


@Composable
fun StoreScreen(navHostController: NavHostController, furnitureViewModel: FurnitureViewModel) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    val uiState by furnitureViewModel.storeUiState.collectAsState()
    val coins by furnitureViewModel.coins.collectAsState()
    val inventoryUiState by furnitureViewModel.inventoryUiState.collectAsState()

    val ownedFurnitureIds = remember(inventoryUiState) {
        when (inventoryUiState) {
            is UserInventoryUiState.Success -> {
                (inventoryUiState as UserInventoryUiState.Success).ownedFurniture.map { it.id }
            }
            else -> emptyList()
        }
    }


    // Cargar las monedas y el catálogo de muebles cuando el usuario entra en la tienda
    LaunchedEffect(userId) {
        if (userId != null) {
            furnitureViewModel.loadUserCoins(userId)
            furnitureViewModel.loadUserInventory()
        }
        furnitureViewModel.loadFurnitureCatalog()
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            GoBackArrow(
                onClick = {
                    navHostController.navigate("homeScreen") {
                        popUpTo("homeScreen") { inclusive = true }
                    }
                },
                isBrown = false,
                title = "Tienda"
            )

            // Mostrar las monedas del usuario
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$coins",
                    color = White,
                    modifier = Modifier.padding(start = 5.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }


            // LazyGrid para mostrar los muebles
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .border(1.dp, White),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                when (uiState) {
                    is StoreFurnitureUiState.Loading -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = White)
                            }
                        }
                    }

                    is StoreFurnitureUiState.Error -> {
                        val message = (uiState as StoreFurnitureUiState.Error).message
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Error: $message",
                                color = White,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }

                    is StoreFurnitureUiState.Success -> {
                        val groupedFurniture = (uiState as StoreFurnitureUiState.Success).furnitureList

                        // Iterar sobre el mapa explícitamente
                        groupedFurniture.forEach { (theme, furnitureList) ->
                            // Mostrar el tema como título
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = theme.uppercase(),
                                    color = White,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                            }

                            // Mostrar los muebles de ese tema
                            gridItems(furnitureList) { furniture ->
                                FurnitureCard(
                                    furniture = furniture,
                                    userCoins = coins,
                                    userFurnitureIds = ownedFurnitureIds
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

