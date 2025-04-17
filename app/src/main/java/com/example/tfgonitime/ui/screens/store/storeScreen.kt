package com.example.tfgonitime.ui.screens.store

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.storeComp.FurnitureCard
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.FurnitureUiState
import com.example.tfgonitime.viewmodel.FurnitureViewModel
import androidx.compose.foundation.lazy.grid.items as gridItems

@Composable
fun StoreScreen(navHostController: NavHostController, furnitureViewModel: FurnitureViewModel) {
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, bottom = 80.dp)
                    .border(1.dp, White),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (uiState) {
                    is FurnitureUiState.Loading -> {
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

                    is FurnitureUiState.Error -> {
                        val message = (uiState as FurnitureUiState.Error).message
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Error: $message",
                                color = White,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }

                    is FurnitureUiState.Success -> {
                        val grouped = (uiState as FurnitureUiState.Success).furnitureByTheme

                        grouped.forEach { (theme, items) ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = theme.uppercase(),
                                    color = White,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                            }

                            gridItems(items) { furniture ->
                                FurnitureCard(furniture = furniture)
                            }
                        }
                    }
                }
            }
        }
    }
}

