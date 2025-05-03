package com.example.tfgonitime.ui.screens.petCatalogue

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.homeComp.InteractiveHome
import com.example.tfgonitime.ui.components.inventory.InventoryCard
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.UserInventoryUiState

@Composable
fun PetCatalogueScreen(navHostController: NavHostController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
    ) {
        // Parte superior (50% de la pantalla)
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
                    modifier=Modifier.fillMaxSize(),contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(R.drawable.head_daifuku),
                        contentDescription = "Mascota",
                        modifier = Modifier
                            .size(80.dp)
                            .offset(y = 80.dp)
                    )
                    Image(
                        painter = painterResource(R.drawable.coffee_jelly_body_1),
                        contentDescription = "Mascota",
                        modifier = Modifier
                            .size(150.dp)
                    )

                }
            }
        }

        // Parte inferior (área para la lista de inventario)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(White)
        ) {

        }
    }
}