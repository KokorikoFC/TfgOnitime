package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
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
import com.example.tfgonitime.data.model.TaskGroup
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun AddTaskGroupScreen(navHostController: NavHostController, groupViewModel: GroupViewModel, userId: String) {

    var groupName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("Green") }

    // Mensajes de error
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

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

            GoBackArrow(
                onClick = {
                    navHostController.navigate("homeScreen") {
                        popUpTo("homeScreen") { inclusive = true }
                    }
                },
                isBrown = true,
                title = "Nuevo Grupo"
            )


            Spacer(modifier = Modifier.padding(20.dp))


            CustomTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = "Nombre del Grupo",
                placeholder = "Escribe el nombre del grupo",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.padding(20.dp))

            Text("Selecciona un color:")

            FlowRow(
                mainAxisSpacing = 10.dp,
                crossAxisSpacing = 10.dp,
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
            ) {
                colorMap.forEach { (colorName, color) ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
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
            // Box con los círculos de selección de color

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
                .padding(top = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedMessage(
                message = errorMessage,
                isVisible = isErrorVisible,
                onDismiss = { isErrorVisible = false },
                isWhite = false
            )
        }
    }

}

