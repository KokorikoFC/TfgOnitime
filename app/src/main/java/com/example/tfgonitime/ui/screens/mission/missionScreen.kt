package com.example.tfgonitime.ui.screens.mission

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MissionScreen(navHostController: NavHostController, missionViewModel: Any) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
}