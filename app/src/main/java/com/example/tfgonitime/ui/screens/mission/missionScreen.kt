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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mission
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomBottomNavBar
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
        containerColor = Color.White,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Parte superior (40% de la pantalla)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.35f)
                            .background(White),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "Misiones",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                            color = DarkBrown
                        )
                    }

                    // Parte inferior (60% con LazyColumn)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(Green.copy(alpha = 0.7f))
                    ) {
                        // Mostrar indicador de carga si las misiones están vacías o cargando
                        if (missions.isEmpty()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = DarkBrown
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                            ) {
                                // Listar misiones
                                itemsIndexed(missions) { index, mission ->
                                    MissionItem(mission = mission) {
                                        missionViewModel.completeMission(userId, mission.id)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun MissionItem(mission: Mission, onComplete: () -> Unit) {
    var isChecked by remember { mutableStateOf(mission.isCompleted) }
    val backgroundColor = if (mission.isCompleted) Green.copy(alpha = 0.3f) else Color.White // Elige el color que quieras para completado

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor) // Aplicamos el color de fondo condicional aquí
            .padding(16.dp)
            .padding(bottom = 8.dp)
            .clickable { onComplete() },
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = mission.description,
            style = TextStyle(fontSize = 16.sp, color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Recompensa: ${mission.reward} puntos",
            style = TextStyle(fontSize = 14.sp, color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "id: ${mission.id} ",
            style = TextStyle(fontSize = 14.sp, color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        mission.imageUrl?.let {
            // Aquí puedes cargar la imagen desde la URL
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Gray)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    onComplete()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isChecked) "Completada" else "No completada",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
            )
        }
    }
}



