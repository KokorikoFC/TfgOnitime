package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
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
fun AddTaskGroupScreen(
    navHostController: NavHostController,
    groupViewModel: GroupViewModel,
    userId: String
) {

    var groupName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("Green") }

    // Mensajes de error
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    // Mapa de colores
    val colorMap = mapOf(
        "LightRed" to LightRed,
        "LightOrange" to LightOrange,
        "Yellow" to Yellow,
        "LightGreen" to LightGreen,
        "LightBlue" to LightBlue,
        "LightPink" to LightPink,
        "Purple" to Purple,
        "LightPurple" to LightPurple,
        "LightBrown" to LightBrown
    )


    // Pantalla con el botón fijo en el fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
        ) {

            GoBackArrow(
                onClick = {
                    navHostController.navigate("homeScreen") {
                        popUpTo("homeScreen") { inclusive = true }
                    }
                },
                isBrown = true,
                title = "Nuevo Grupo"
            )


            CustomTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = "Nombre del Grupo",
                placeholder = "Escribe el nombre del grupo",
                modifier = Modifier.padding(bottom = 16.dp, top = 10.dp)
            )

            Spacer(modifier = Modifier.padding(20.dp))

            Text("Selecciona un color:")

            Spacer(modifier = Modifier.padding(10.dp))
            FlowRow(
                mainAxisSpacing = 10.dp,
                crossAxisSpacing = 10.dp,
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
            ) {
                colorMap.forEach { (colorName, color) ->
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .shadow(4.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(White)
                            .then(
                                if (selectedColor == colorName) {
                                    Modifier.border(2.dp, color, RoundedCornerShape(10.dp)) // solo si está seleccionado
                                } else {
                                    Modifier
                                }
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                selectedColor = colorName
                            }
                    )
                    {
                        // Parte inferior (tarjeta de código)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .fillMaxHeight(0.4f)
                                .background(LightBeige),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "#" + Integer.toHexString(color.toArgb()).uppercase()
                                    .takeLast(6),
                                color = Gray,
                                fontSize = 12.sp
                            )
                        }

                        // Parte superior (tarjeta de color que flota)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .fillMaxHeight(0.65f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color)
                        )
                    }
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

