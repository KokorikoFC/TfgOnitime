package com.example.tfgonitime.ui.screens.login

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomPasswordField
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.StreakViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun LoginScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val streakViewModel = StreakViewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {
        // Primera columna con muñeco y texto
        PetOnigiriWithDialogue(showBubbleText = true,
            bubbleText = stringResource(R.string.welcome_message))

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
                Text(
                    text = stringResource(R.string.login_title),
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.email_hint),
                    placeholder = stringResource(R.string.email_placeholder),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.password_hint),
                    placeholder = stringResource(R.string.password_placeholder),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextButton(onClick = { navHostController.navigate("changePasswordScreen")},colors = ButtonDefaults.textButtonColors(
                    contentColor = Green
                )) {
                    Text(text = stringResource(R.string.forgot_password))
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(
                    onClick = {
                        authViewModel.login(
                            email,
                            password,
                            context = context,
                            onSuccess = {
                                val userId = FirebaseAuth.getInstance().currentUser?.uid
                                if (userId != null) {
                                    streakViewModel.checkStreakAndNavigate(userId) { route ->
                                        navHostController.navigate(route) {
                                            popUpTo("changePasswordScreen") { inclusive = true }
                                        }
                                    }
                                }
                            },
                            onError = { error ->
                                errorMessage = error
                                isErrorVisible = true
                            }
                        )
                    },
                    buttonText = stringResource(R.string.login_button),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(R.string.no_account), color = DarkBrown)
                    TextButton(onClick = { navHostController.navigate("signUpNameScreen") },colors = ButtonDefaults.textButtonColors(
                        contentColor = Green
                    )) {
                        Text(text = stringResource(R.string.register_link))
                    }
                }

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
                onDismiss = { isErrorVisible = false },
                isWhite = true
            )
        }

        // Row fijo al fondo, fuera del formulario
        DecorativeBottomRow(
            modifier = Modifier.align(Alignment.BottomCenter) // Alineación correcta
        )
    }
}
