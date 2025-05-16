package com.example.tfgonitime.ui.screens.diary

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.data.repository.ChatRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.HeaderArrow
import com.example.tfgonitime.ui.components.diaryComp.MoodOptions
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun MoodSelectionScreen(
    navHostController: NavHostController,
    selectedDate: LocalDate,
    diaryViewModel: DiaryViewModel,
) {
    val diaryEntry = remember { mutableStateOf("") }
    val selectedMood = remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    val chatRepository = ChatRepository()
    val userRepository = UserRepository()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp), // espacio para el botón
            verticalArrangement = Arrangement.Top
        ) {
            HeaderArrow(
                onClick = { navHostController.popBackStack() },
                title = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "Registrar estado de ánimo",
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
                value = diaryEntry.value,
                onValueChange = { diaryEntry.value = it },
                placeholder = {
                    Text(
                        text = "Escribe tu entrada del día",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f) // o cualquier color que prefieras
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
                .padding(20.dp)
        ) {
            CustomButton(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        val date = selectedDate.toString()
                        val moodType = selectedMood.value
                        val entry = diaryEntry.value

                        diaryViewModel.viewModelScope.launch {
                            try {
                                val userResult = userRepository.getUserName(userId)
                                val user = userResult.getOrNull() ?: ""

                                val generatedLetter = chatRepository.sendDiaryLetter(
                                    userName = user,
                                    diaryEntry = entry,
                                    moodType = moodType,
                                    moodDate = date
                                )

                                val mood = Mood(
                                    id = userId,
                                    moodDate = date,
                                    moodType = moodType,
                                    diaryEntry = entry,
                                    generatedLetter = generatedLetter
                                )

                                diaryViewModel.addMood(
                                    userId,
                                    mood,
                                    onSuccess = {
                                        navHostController.popBackStack()
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        isErrorVisible = true
                                    }
                                )
                            } catch (e: Exception) {
                                errorMessage = "Ocurrió un error al guardar tu diario"
                                isErrorVisible = true
                            }
                        }
                    } else {
                        errorMessage = "Usuario no autenticado"
                        isErrorVisible = true
                    }
                },
                buttonText = "Guardar",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                backgroundColor = Green,
                textColor = White
            )

            AnimatedMessage(
                message = errorMessage,
                isVisible = isErrorVisible,
                onDismiss = { isErrorVisible = false },
                isWhite = false
            )
        }
    }
}

