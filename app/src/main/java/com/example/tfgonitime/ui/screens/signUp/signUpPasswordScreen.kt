package com.example.tfgonitime.ui.screens.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomPasswordField
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpPasswordScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {

    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {
        GoBackArrow(onClick = {
            navHostController.navigate("signUpEmailScreen") {
                popUpTo("signUpPasswordScreen") { inclusive = true }
            }
        }, isBrown = false, title = "")

        // Primera columna con muñeco y texto
        PetOnigiriWithDialogue(
            showBubbleText = true,
            bubbleText = stringResource(R.string.register_password_bubble)
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
                    text =stringResource(R.string.register_password_prompt),
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = DarkBrown,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(60.dp))

                CustomPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.password_hint),
                    placeholder = stringResource(R.string.password_hint),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomPasswordField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    label = stringResource(R.string.confirm_password_hint),
                    placeholder = stringResource(R.string.confirm_password_hint),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

            }

            CustomButton(
                onClick = {
                    // Validamos y configuramos la contraseña antes de proceder con el registro
                    authViewModel.setPassword(
                        password,
                        repeatPassword,
                        context = context,
                        onSuccess = {
                            // Si la contraseña es válida, procedemos con el registro
                            authViewModel.viewModelScope.launch {
                                authViewModel.signupUser { success, errorMessage ->
                                    if (success) {
                                        // Navegamos al Home si la creación del usuario fue exitosa
                                        navHostController.navigate("homeScreen") {
                                            popUpTo("signUpPasswordScreen") { inclusive = true }
                                        }

                                    }
                                }
                            }
                        },
                        onError = { error ->
                            // Mostramos el mensaje de error si la contraseña no es válida
                            errorMessage = error
                            isErrorVisible = true
                        }
                    )
                },
                buttonText =stringResource(R.string.signup_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp, start = 30.dp, end = 30.dp)
            )
        }


        // Row fijo al fondo, fuera del formulario
        DecorativeBottomRow(
            modifier = Modifier.align(Alignment.BottomCenter)
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
                onDismiss = { isErrorVisible = false },
                isWhite = false
            )
        }
    }
}
