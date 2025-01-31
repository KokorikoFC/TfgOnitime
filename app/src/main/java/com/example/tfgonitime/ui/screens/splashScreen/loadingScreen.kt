package com.example.tfgonitime.ui.screens.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun LoadingScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    val isAuthenticated = authViewModel.isAuthenticated.collectAsState().value

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset(y = 15.dp), // Baja la caja para superponerse
            contentAlignment = Alignment.BottomEnd // Alinea el contenido al fondo a la derecha
        ) {


            Image(
                painter = painterResource(id = R.drawable.ui_singup_onigiritalking), // Ruta de la imagen
                contentDescription = "Muñeco apoyado",
                modifier = Modifier.size(130.dp),
                contentScale = ContentScale.Fit
            )

        }
        LaunchedEffect(isAuthenticated) {
            when (isAuthenticated) {
                true -> navHostController.navigate("homeScreen") {
                    popUpTo("splashScreen") { inclusive = true }
                }
                false -> navHostController.navigate("loginScreen") {
                    popUpTo("splashScreen") { inclusive = true }
                }
                null -> { /* Loading Screen */ }
            }
        }



        androidx.compose.material3.CircularProgressIndicator()
    }}