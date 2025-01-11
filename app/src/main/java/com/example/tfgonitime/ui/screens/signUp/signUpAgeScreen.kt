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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.AuthViewModel
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView

@Composable
fun SignUpAgeScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {

    var errorMessage by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

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
                Text(
                    text = "Por favor introduce tu fecha de nacimiento",
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = DarkBrown,
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                WheelDatePickerView(
                    showDatePicker = showDatePicker,
                    height = 200.dp,
                    dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
                    rowCount = 3,
                    onDoneClick = {
                        showDatePicker=false
                    }
                )

                CustomButton(
                    onClick = {

                        navHostController.navigate("signUpEmailScreen") {
                            popUpTo("signUpAgeScreen") { inclusive = true }
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
