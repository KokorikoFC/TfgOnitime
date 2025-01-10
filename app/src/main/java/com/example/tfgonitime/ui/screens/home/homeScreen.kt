package com.example.tfgonitime.ui.screens.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.viewmodel.AuthViewModel


@Composable
fun HomeScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    items(50) { index -> // Genera 50 elementos para llenar la pantalla
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),

                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Elemento #$index",
                                    color = Color.Black
                                )

                                Button(
                                    onClick = {
                                        // Acción al presionar el botón
                                        navHostController.navigate("notificationScreen")
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(text = "Ir a Notificaciones")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
