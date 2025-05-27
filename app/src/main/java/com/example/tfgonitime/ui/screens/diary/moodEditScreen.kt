package com.example.tfgonitime.ui.screens.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.HeaderArrow
import com.example.tfgonitime.ui.components.diaryComp.MoodOptions
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MoodEditScreen(
    navHostController: NavHostController,
    diaryViewModel: DiaryViewModel,
    authViewModel: AuthViewModel,
    moodDate: String,
) {
    val mood by diaryViewModel.selectedMood.collectAsState()
    var diaryEntry by remember { mutableStateOf("") }
    val selectedMood = remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current

    LaunchedEffect(moodDate, userId) {
        println ("El user id en MoodEditScreen es: $userId")
        if (userId != null) {
            diaryViewModel.getMoodById(moodDate, userId)
        }
    }

    LaunchedEffect(mood) {
        mood?.let {
            if (selectedMood.value.isEmpty()) selectedMood.value = it.moodType
            if (diaryEntry.isEmpty()) diaryEntry = it.diaryEntry
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Top
        ) {
            HeaderArrow(
                onClick = {
                    navHostController.navigate("homeScreen") {
                        popUpTo("homeScreen") { inclusive = true }
                    }
                },
                title = mood?.let { formatDateForDisplay(it.moodDate) } ?: ""
            )

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = stringResource(R.string.mood_edit),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(35.dp))

            MoodOptions(selectedMood)

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = diaryEntry,
                onValueChange = { diaryEntry = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.mood_textfield_edit),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, Brown, shape = MaterialTheme.shapes.medium),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onPrimary
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        // Botón en la parte inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 60.dp)
        ) {
            CustomButton(
                onClick = {
                    mood?.let { updatedMood ->
                        val newMood = updatedMood.copy(
                            moodType = selectedMood.value,
                            diaryEntry = diaryEntry
                        )
                        if (userId != null) {
                            diaryViewModel.updateMood(
                                newMood,
                                userId = userId,
                                context = context,
                                onSuccess = {
                                    navHostController.popBackStack()
                                },
                                onError = { error ->
                                    errorMessage = error
                                    isErrorVisible = true
                                }
                            )
                        }
                    }
                },
                buttonText = stringResource(R.string.mood_save_changes),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                backgroundColor = Green,
                textColor = White
            )
        }

        // AnimatedMessage arriba, estilo SignUpNameScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
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


fun formatDateForDisplay(dateString: String): String {
    // Definir el formato de fecha de entrada y salida
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Parsear la fecha y luego formatearla
    val date = LocalDate.parse(dateString, inputFormatter)
    return date.format(outputFormatter)
}