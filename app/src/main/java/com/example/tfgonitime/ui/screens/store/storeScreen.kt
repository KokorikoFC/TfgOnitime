package com.example.tfgonitime.ui.screens.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.FurnitureUiState
import com.example.tfgonitime.viewmodel.FurnitureViewModel


@Composable
fun StoreScreen(navHostController: NavHostController) {
    val furnitureViewModel: FurnitureViewModel = viewModel()
    val uiState by furnitureViewModel.uiState.collectAsState()

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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp, bottom = 80.dp)
                    .border(1.dp, White),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (uiState) {
                    is FurnitureUiState.Loading -> {
                        item {
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

                    is FurnitureUiState.Error -> {
                        val message = (uiState as FurnitureUiState.Error).message
                        item {
                            Text(
                                text = "Error: $message",
                                color = White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    is FurnitureUiState.Success -> {
                        val grouped = (uiState as FurnitureUiState.Success).furnitureByTheme

                        grouped.forEach { (theme, items) ->
                            item {
                                Text(
                                    text = theme.uppercase(),
                                    color = White,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                            }

                            items(items) { furniture ->
                                FurnitureCard(furniture = furniture)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FurnitureCard(furniture: Furniture) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .border(1.dp, White)
            .background(White)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen de placeholder desde drawable
        Image(
            painter = painterResource(id = R.drawable.head_taiyaki),
            contentDescription = "Furniture Image",
            modifier = Modifier.size(80.dp)
        )

        Column {
            Text(furniture.name, color = Brown)
            Text("${furniture.price} monedas", color = Brown)
        }
    }
}
