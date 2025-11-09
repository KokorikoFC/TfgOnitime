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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
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
fun SignUpNameScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    var userName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {
        // Primera columna con muñeco y texto
        PetOnigiriWithDialogue(
            showBubbleText = true,
            bubbleText = stringResource(R.string.signUp_Bubble)
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
                    text = stringResource(R.string.register_welcome),
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.register_name_prompt),
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = DarkBrown,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(60.dp))

                CustomTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = stringResource(R.string.name_hint),
                    placeholder = stringResource(R.string.name_placeholder),
                    textColor = DarkBrown,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

            }
            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(
                onClick = {
                    // Establecer el nombre en el ViewModel
                    authViewModel.setUserName(userName,context = context, onSuccess = {
                        navHostController.navigate("signUpGenderScreen") {
                            popUpTo("signUpNameScreen") { inclusive = true }
                        }
                    },onError = { error ->
                        errorMessage = error
                        isErrorVisible = true
                    })
                },
                buttonText = stringResource(R.string.continue_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp, start = 30.dp, end = 30.dp)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.already_account), color = DarkBrown)
                TextButton(
                    onClick = { navHostController.navigate("loginScreen") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Green
                    )
                ) {
                    Text(stringResource(R.string.login_link))
                }
            }
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
