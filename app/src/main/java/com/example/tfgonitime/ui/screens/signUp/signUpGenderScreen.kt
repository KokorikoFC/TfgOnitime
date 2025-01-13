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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {
        GoBackArrow(onClick ={ navHostController.navigate("signUpNameScreen") {
            popUpTo("signUpGenderScreen") { inclusive = true }
        }}, isBrown = false)

        // Primera columna con muñeco y texto
        PetOnigiriWithDialogue(showBubbleText = false,
            bubbleText = "...")

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
                    text = "Selecciona tu género",
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = DarkBrown,
                    )
                )
                Spacer(modifier = Modifier.height(40.dp))

                GenderSelectableChip(
                    gender = "Masculino",
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
                Spacer(modifier = Modifier.height(30.dp))
                GenderSelectableChip(
                    gender = "Femenino",
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
                Spacer(modifier = Modifier.height(30.dp))
                GenderSelectableChip(
                    gender = "Otro",
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }


            }
            CustomButton(
                onClick = {

                    if (selectedGender.isNotEmpty()) {
                        // Establecer el nombre en el ViewModel
                        authViewModel.setUserGender(selectedGender)

                        // Navegar a la siguiente pantalla
                        navHostController.navigate("signUpAgeScreen") {
                            popUpTo("signUpGenderScreen") { inclusive = true }
                        }
                    } else {
                        errorMessage = "Debes elegir una opción"
                    }
                }
                ,
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
    }
}