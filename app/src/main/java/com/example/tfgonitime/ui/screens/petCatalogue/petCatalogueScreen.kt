package com.example.tfgonitime.ui.screens.petCatalogue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White

@Composable
fun PetCatalogueScreen(navHostController: NavHostController) {

    Box(
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
            title = "Mascotas",

            )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 80.dp)
                .border(1.dp, Brown),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

        }

    }
}