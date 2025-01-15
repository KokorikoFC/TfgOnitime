package com.example.tfgonitime.ui.screens.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun SplashScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Green),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Logo at the top
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(280.dp)
                .padding(top = 32.dp), // Adjust the top padding
            contentScale = ContentScale.Fit
        )

        // Splash art in the middle
        Image(
            painter = painterResource(id = R.drawable.splash_art),
            contentDescription = "Splash Art",
            modifier = Modifier
                .size(300.dp),
            contentScale = ContentScale.Fit

        )
        Spacer(modifier = Modifier.height(70.dp))

        // Button at the bottom
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .size(width = 220.dp, height = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.start_splash_btn),
                contentDescription = "Start Button",
                modifier = Modifier.size(500.dp)
                .clickable {
                navHostController.navigate("loadingScreen")

            },
                contentScale = ContentScale.Fit,


            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
