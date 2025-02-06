package com.example.tfgonitime.ui.screens.signUp

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun SignUpEmailScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var repeatEmail by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {
        GoBackArrow(onClick = {
            navHostController.navigate("signUpAgeScreen") {
                popUpTo("signUpEmailScreen") { inclusive = true }
            }
        }, isBrown = false, title = "")

        // Primera columna con muñeco y texto
        PetOnigiriWithDialogue(
            showBubbleText = false,
            bubbleText = "..."
        )

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
                    .padding(start = 30.dp, end = 30.dp, top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Text(
                    text = "¿Cuál es tu correo electrónico?",
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = DarkBrown,
                    )
                )
                Spacer(modifier = Modifier.height(80.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo",
                    placeholder = "Introduce tu correo",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    value = repeatEmail,
                    onValueChange = { repeatEmail = it },
                    label = "Repite el correo",
                    placeholder = "Repite tu correo",
                    modifier = Modifier.padding(bottom = 16.dp)
                )


            }


            CustomButton(
                onClick = {
                    // Llamamos a la función setUserEmail para validar el correo
                    authViewModel.setUserEmail(
                        email,
                        repeatEmail,
                        context = context,
                        onSuccess = {
                            // Si no hay error, procedemos con la navegación
                            navHostController.navigate("signUpPasswordScreen") {
                                popUpTo("signUpEmailScreen") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            // Si hay error, mostramos el mensaje de error
                            errorMessage = error
                            isErrorVisible = true
                        }
                    )
                },
                buttonText = "Confirmar",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp, start = 30.dp, end = 30.dp)
            )
        }

        // Row fijo al fondo, fuera del formulario
        DecorativeBottomRow(
            modifier = Modifier.align(Alignment.BottomCenter) // Alineación correcta
        )

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
    }

}