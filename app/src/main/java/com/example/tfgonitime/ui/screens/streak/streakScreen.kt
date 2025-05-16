package com.example.tfgonitime.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.viewmodel.StreakViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.theme.Brown

@Composable
fun StreakScreen(navHostController: NavHostController, streakViewModel: StreakViewModel) {
    val currentStreak by streakViewModel.currentStreak.collectAsState()
    val longestStreak by streakViewModel.longestStreak.collectAsState()
    val loadingState by streakViewModel.loadingState.collectAsState()
    val updateSuccess by streakViewModel.updateSuccess.collectAsState()
    val updateError by streakViewModel.updateError.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let {
            streakViewModel.loadStreak(it)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, top = 80.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.streak_title),
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(150.dp))

            if (loadingState) {
                CircularProgressIndicator()
            } else {
                val dayNames = listOf("L", "M", "X", "J", "V", "S", "D")

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(7) { dayIndex ->
                        val isCompleted = dayIndex < currentStreak
                        DayCircle(isCompleted = isCompleted, dayName = dayNames[dayIndex])
                    }
                }


                Spacer(modifier = Modifier.height(80.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+50",
                        style = TextStyle(fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )


                }
            }
        }

        // Botón en la parte inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 20.dp, end = 20.dp)
        ) {
            CustomButton(
                onClick = {
                    userId?.let {
                        streakViewModel.onOpenAppTodayClicked(it)
                    }
                },
                buttonText = stringResource(R.string.open_app_today),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Mostrar error si hay problemas con la actualización
        if (updateError != null) {
            LaunchedEffect(updateError) {
                Log.e("StreakScreen", "Error al actualizar racha")
            }
        }

        LaunchedEffect(updateSuccess) {
            if (updateSuccess) {
                navHostController.navigate("homeScreen") {
                    popUpTo("streakScreen") { inclusive = true }
                }
                streakViewModel.clearUpdateState()
            }
        }
    }
}


@Composable
fun DayCircle(isCompleted: Boolean, dayName: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Image(
                    painter = painterResource(id = R.drawable.head_onigiri),
                    contentDescription = "Cabeza de onigiri",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp) // Se ajusta al mismo tamaño que el círculo
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dayName,
            style = TextStyle(fontSize = 12.sp),
            color = Brown,
            fontWeight = FontWeight.SemiBold
        )
    }
}

