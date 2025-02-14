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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.GenderSelectableChip
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun SignUpGenderScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {

    var errorMessage by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {

            GoBackArrow(onClick = {
                navHostController.navigate("signUpNameScreen") {
                    popUpTo("signUpGenderScreen") { inclusive = true }
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
                    text = stringResource(R.string.gender_prompt),
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = DarkBrown,
                    )
                )
                Spacer(modifier = Modifier.height(40.dp))

                GenderSelectableChip(
                    displayText = stringResource(R.string.gender_male), // Texto visible para el usuario
                    internalValue = "Masculino", // Valor interno en español
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
                Spacer(modifier = Modifier.height(30.dp))
                GenderSelectableChip(
                    displayText = stringResource(R.string.gender_female), // Texto visible para el usuario
                    internalValue = "Femenino", // Valor interno en español
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
                Spacer(modifier = Modifier.height(30.dp))
                GenderSelectableChip(
                    displayText = stringResource(R.string.gender_other), // Texto visible para el usuario
                    internalValue = "Otro", // Valor interno en español
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
            }
            CustomButton(
                onClick = {
                    // Establecer el nombre en el ViewModel
                    authViewModel.setUserGender(selectedGender, context = context, onSuccess = {
                        navHostController.navigate("signUpAgeScreen") {
                            popUpTo("signUpGenderScreen") { inclusive = true }
                        }
                    }, onError = { error ->
                        errorMessage = error
                        isErrorVisible = true
                    })
                },
                buttonText = stringResource(R.string.continue_button),
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