package com.example.tfgonitime.ui.screens.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Divider
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
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.DecorativeBottomRow
import com.example.tfgonitime.ui.components.PetOnigiriWithDialogue
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.AuthViewModel
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.Date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate as JavaLocalDate


@Composable
fun SignUpAgeScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {

    var errorMessage by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

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
                    .padding(start = 30.dp,end = 30.dp,top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Por favor introduce tu fecha de nacimiento",
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = DarkBrown,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(100.dp))

                // Librería kmp-date-time-picker
                WheelDatePickerView(
                    showDatePicker = showDatePicker,
                    height = 200.dp,
                    dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
                    rowCount = 3,
                    title= "Fecha de nacimiento",
                    showShortMonths = true,
                    yearsRange = 1920..LocalDate.now().year,
                    onDoneClick = { snappedDate ->
                        // Aquí la fecha seleccionada es el 'snappedDate'
                        selectedDate = snappedDate
                        showDatePicker = false
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )

                val monthNumber = selectedDate?.monthNumber ?: "MM"
                val dayOfMonth = selectedDate?.dayOfMonth ?: "DD"
                val year = selectedDate?.year ?: "YYYY"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp) // Espacio de 16dp entre las columnas
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable {
                                showDatePicker = true
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("$monthNumber")
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            thickness = 2.dp
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showDatePicker = true
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("$dayOfMonth")
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            thickness = 2.dp
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f).clickable {
                            showDatePicker = true
                        },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("$year")
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                ,
                            thickness = 2.dp
                        )
                    }
                }



                Spacer(modifier = Modifier.height(20.dp))


            }
            CustomButton(
                onClick = {
                    navHostController.navigate("signUpEmailScreen") {
                        popUpTo("signUpAgeScreen") { inclusive = true }
                    }
                },
                buttonText = "Confirmar",
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 40.dp, start = 30.dp, end = 30.dp)

            )
        }


        // Row fijo al fondo, fuera del formulario
        DecorativeBottomRow(
            modifier = Modifier.align(Alignment.BottomCenter) // Alineación correcta
        )
    }
}

