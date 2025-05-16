package com.example.tfgonitime.ui.screens.missionScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mission
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.CustomCheckBox
import com.example.tfgonitime.ui.components.missionComp.MissionItem
import com.example.tfgonitime.ui.components.taskComp.CustomFloatingButton
import com.example.tfgonitime.ui.components.taskComp.TaskItem
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.example.tfgonitime.viewmodel.MissionViewModel
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MissionScreen(
    navHostController: NavHostController,
    missionViewModel: MissionViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    val missions by missionViewModel.missionsState.collectAsState()

    // Si el usuario no está autenticado, mostramos un mensaje
    if (userId == null) {
        Log.e("MissionScreen", "Error: userId es null")
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Por favor inicia sesión para ver tus misiones.", color = Color.Red)
        }
        return
    } else {
        // Solo cargar misiones si el userId es válido
        LaunchedEffect(userId) {
            missionViewModel.loadMissions(userId)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Misiones",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Mostrar indicador de carga si las misiones están vacías o cargando
                    if (missions.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                            color = DarkBrown
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 20.dp, end = 20.dp, top = 50.dp)
                        ) {
                            item {
                                Text(
                                    text = "Complétalas todas",
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp
                                    ),
                                    color = Brown,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(35.dp))
                            }
                            // Listar misiones
                            itemsIndexed(missions) { index, mission ->
                                MissionItem(
                                    mission = mission,
                                    onComplete = {
                                        // Now call the function to check all missions progress
                                        missionViewModel.triggerMissionCompletionCheck(userId)
                                    },
                                    onClaimReward = {
                                        missionViewModel.claimMissionReward(userId, mission.id)
                                    }
                                )
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                        }
                    }
                }
            }
        }
    )
}


