package com.example.tfgonitime.ui.screens.signUp

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.GenderSelectableChip
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
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
        Button(
            onClick = {
                navHostController.navigate("signUpNameScreen") {
                }
            },
        ) {

        }
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
                Text(
                    text = "Selecciona tu género",
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = DarkBrown,
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                GenderSelectableChip(
                    gender = "Masculino",
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                GenderSelectableChip(
                    gender = "Femenino",
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                GenderSelectableChip(
                    gender = "Otro",
                    selectedGender = selectedGender,
                    onSelect = { selectedGender = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(
                    onClick = {
                        navHostController.navigate("signUpAgeScreen") {
                            popUpTo("signUpGenderScreen") { inclusive = true }
                        }
                    },
                    buttonText = "Confirmar",
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }

        // Row fijo al fondo, fuera del formulario
        DecorativeBottomRow(
            modifier = Modifier.align(Alignment.BottomCenter) // Alineación correcta
        )
    }
}