package com.example.tfgonitime.ui.screens.splashScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun SplashScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    val isAuthenticated = authViewModel.isAuthenticated.collectAsState().value

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
}