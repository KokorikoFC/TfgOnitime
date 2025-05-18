package com.example.tfgonitime.ui.screens.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.ui.components.DeleteConfirmationDialog
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.storeComp.FurnitureCard
import com.example.tfgonitime.ui.components.storeComp.PurchaseConfirmationDialog
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.DarkBrown
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
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val selectedFurniture = remember { mutableStateOf<Furniture?>(null) }

    // Cargar las monedas y el catálogo de muebles cuando el usuario entra en la tienda
    LaunchedEffect(userId) {
        if (userId != null) {
            furnitureViewModel.loadUserCoins(userId)
            furnitureViewModel.loadUserInventory()
        }
        furnitureViewModel.loadFurnitureCatalog()
    }

    // Mostrar el diálogo de confirmación para la compra
    if (showDialog && selectedFurniture.value != null) {
        PurchaseConfirmationDialog(
            showDialog = showDialog,
            furnitureName = selectedFurniture.value?.name.orEmpty(),
            onDismiss = { setShowDialog(false) },
            onConfirm = {
                selectedFurniture.value?.let { furniture ->
                    furnitureViewModel.purchaseItem(furniture)
                }
                setShowDialog(false)
            }
        )

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
                title = stringResource(R.string.store_title)
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
                    .padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp),
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
                        val groupedFurniture =
                            (uiState as StoreFurnitureUiState.Success).furnitureList

                        // Iterar sobre el mapa explícitamente
                        groupedFurniture.forEach { (theme, furnitureList) ->

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = theme.uppercase(),
                                    color = DarkBrown,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                            }

                            // Mostrar los muebles de ese tema
                            gridItems(furnitureList) { furniture ->

                                // Lógica para comprobar si el mueble está en el inventario
                                val isOwned = inventoryUiState is UserInventoryUiState.Success &&
                                        (inventoryUiState as UserInventoryUiState.Success).ownedFurniture
                                            .any { it.id == furniture.id }

                                // Verificar si el usuario tiene suficientes monedas
                                val isAffordable = coins >= furniture.price

                                // Verifica si el mueble puede ser comprado (no está en el inventario y tiene monedas suficientes)
                                val isClickable = !isOwned && isAffordable

                                // Aplicar color de fondo o opacidad si no es interactuable
                                val cardBackgroundColor = if (!isClickable) Brown else White
                                val cardOpacity =
                                    if (!isClickable) 0.5f else 1f // Desactivar visualmente si no puede comprarse

                                FurnitureCard(
                                    furniture = furniture,
                                    userCoins = coins,
                                    userFurnitureIds = (inventoryUiState as? UserInventoryUiState.Success)?.ownedFurniture?.map { it.id }
                                        ?: emptyList(),
                                    onClick = {
                                        if (isClickable) {  // Solo permite click si es interactuable
                                            selectedFurniture.value = furniture
                                            setShowDialog(true)
                                        }
                                    },
                                    modifier = Modifier
                                        .background(cardBackgroundColor)
                                )
                            }

                        }
                        item {
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }

                }
            }
        }
    }
}
