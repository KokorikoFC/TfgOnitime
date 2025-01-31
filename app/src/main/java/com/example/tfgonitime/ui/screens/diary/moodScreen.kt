package com.example.tfgonitime.ui.screens.diary

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.components.diaryComp.ComprobarMoodType
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MoodScreen(
    navHostController: NavHostController,
    diaryViewModel: DiaryViewModel,
    moodDate: String,
) {
    val mood by diaryViewModel.selectedMood.collectAsState() // Observa el estado del mood

    // Obtener el mood al iniciar la pantalla
    LaunchedEffect(moodDate) {
        diaryViewModel.getMoodById(moodDate)
    }

    val emojiResId = mapOf(
        "fantastico" to R.drawable.fantastico,
        "feliz" to R.drawable.happy_face,
        "masomenos" to R.drawable.masomenos,
        "triste" to R.drawable.triste,
        "deprimido" to R.drawable.deprimido,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera con flecha de volver y fecha centrada
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { navHostController.popBackStack() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver atrás",
                    tint = Color.Black
                )
            }

            mood?.let {
                Text(
                    text = formatDateForDisplay(it.moodDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.size(24.dp)) // Espaciado para alinear
        }

        Spacer(modifier = Modifier.height(24.dp)) // Espaciado para alinear

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (mood == null) {
                    // Muestra un indicador de carga en lugar de la imagen temporal
                    CircularProgressIndicator(color = Color.Gray)
                } else {
                    val resourceId = emojiResId[mood!!.moodType] ?: R.drawable.happy_face

                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Emoji"
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = ComprobarMoodType(mood!!.moodType),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = mood!!.diaryEntry,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(84.dp))

                    // Botón para leer carta
                    Button(
                        onClick = { /* Acción Leer carta */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        shape = RoundedCornerShape(8.dp), // Ajustar esquinas
                    ) {
                        Text(
                            text = "Leer carta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}




