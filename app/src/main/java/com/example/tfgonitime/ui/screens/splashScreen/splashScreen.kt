package com.example.tfgonitime.ui.screens.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun SplashScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White) // Optional background color for the splash screen
    ) {
        // Logo at the top
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(230.dp)
                .align(Alignment.TopCenter)
                .padding(top = 32.dp), // Adjust the top padding
            contentScale = ContentScale.Fit
        )

        // Splash art in the middle
        Image(
            painter = painterResource(id = R.drawable.splash_art),
            contentDescription = "Splash Art",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center), // Center the splash art
            contentScale = ContentScale.Fit
        )

        // Button at the bottom
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomCenter)
                .clickable {
                    navHostController.navigate("loadingScreen")
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.start_splash_btn),
                contentDescription = "Start Button",
                modifier = Modifier.size(350.dp), // Adjust the size of the button
                contentScale = ContentScale.Fit
            )
        }
    }
}
