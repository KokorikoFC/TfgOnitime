package com.example.tfgonitime.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun ChangePasswordScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {
        // Primera columna con muñeco y texto
        PetOnigiriWithDialogue()

        //FORMULARIO
        Box(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .background(White, shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .padding(bottom = 60.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Introduce tu correo para cambiar tu contraseña",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo",
                    placeholder = "Introduce tu correo",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                CustomButton(
                    onClick = {
                        authViewModel.changePassword(
                            email,
                            onSuccess = {
                                navHostController.navigate("loginScreen") {
                                    popUpTo("changePasswordScreen") { inclusive = true }
                                }
                            },
                            onError = { error ->
                                errorMessage = error // Asigna el mensaje de error
                                isErrorVisible = true // Muestra el mensaje animado
                            }
                        )
                    },
                    buttonText = "Cambiar contraseña",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Recibirás un correo con un enlace para cambiar tu contraseña",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }

        // Caja para el error
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedMessage(
                message = errorMessage,
                isVisible = isErrorVisible,
                onDismiss = { isErrorVisible = false }
            )
        }

        // Row fijo al fondo, fuera del formulario
        DecorativeBottomRow(
            modifier = Modifier.align(Alignment.BottomCenter) // Alineación correcta
        )
    }
}