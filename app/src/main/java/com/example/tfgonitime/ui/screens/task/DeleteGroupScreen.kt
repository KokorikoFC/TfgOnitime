package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.taskComp.GroupBox
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth
@Composable
fun DeleteGroupScreen(
    navHostController: NavHostController,
    groupViewModel: GroupViewModel,
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    val groups by groupViewModel.groupsState.collectAsState()

    if (userId == null) return

    // Llamar a loadGroups() cuando se carga la pantalla y el userId cambia
    LaunchedEffect(userId) {
        groupViewModel.loadGroups(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botón de regreso
        GoBackArrow(
            onClick = {
                navHostController.navigate("homeScreen") {
                    popUpTo("homeScreen") { inclusive = true }
                }
            },
            isBrown = true,
            title = "Eliminar Grupo"
        )

        if (groups.isNotEmpty()) {
            // Si hay grupos, se renderizan uno por uno
            groups.forEach { group ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)).border(2.dp,Brown)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),  // Espaciado dentro de la fila
                        horizontalArrangement = Arrangement.SpaceBetween // Para separar los elementos
                    ) {
                        Text(
                            text = group.groupName,
                            modifier = Modifier.align(Alignment.CenterVertically)  // Alinear al centro vertical
                        )

                        IconButton(
                            onClick = {
                                groupViewModel.deleteGroup(userId ?: "", group.groupId)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar grupo",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        } else {
            Text("No hay grupos disponibles", modifier = Modifier.padding(16.dp))
        }
    }
}



