package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Reminder
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.TaskGroup
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Gray
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.GroupViewModel

@Composable
fun AddTaskGroupScreen(navHostController: NavHostController, groupViewModel: GroupViewModel, userId: String) {

    var groupName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("Green") }

    // Mensajes de error
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }  // Para bloquear el botón mientras carga

    // Mapa de colores
    val colorMap = mapOf(
        "Green" to Green,
        "DarkBrown" to DarkBrown,
        "White" to White,
        "Brown" to Brown,
        "Gray" to Gray
    )

    // Pantalla con el botón fijo en el fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp) ) {
            CustomTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = "Nombre del Grupo",
                placeholder = "Escribe el nombre del grupo",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Box con los círculos de selección de color
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                colorMap.forEach { (colorName, color) ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = colorName }
                            .border(
                                width = 2.dp,
                                color = if (selectedColor == colorName) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        // ---------------BOTÓN FIJO ABAJO----------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            CustomButton(
                onClick = {
                    groupViewModel.addGroup(
                        userId = userId,
                        group = TaskGroup(groupName = groupName, groupColor = selectedColor),
                        onSuccess = { generatedGroupId ->
                            navHostController.popBackStack()
                        },
                        onError = { error ->
                            errorMessage = error
                            isErrorVisible = true
                        }
                    )
                },
                buttonText = "Añadir Grupo",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ---------------MENSAJE DE ERROR----------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            AnimatedMessage(
                message = errorMessage,
                isVisible = isErrorVisible,
                onDismiss = { isErrorVisible = false }
            )
        }
    }
}

