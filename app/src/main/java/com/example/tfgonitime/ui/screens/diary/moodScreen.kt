package com.example.tfgonitime.ui.screens.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.HeaderArrow
import com.example.tfgonitime.ui.components.diaryComp.ComprobarMoodType
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.DiaryViewModel

@Composable
fun MoodScreen(
    navHostController: NavHostController,
    diaryViewModel: DiaryViewModel,
    moodDate: String,
) {
    val mood by diaryViewModel.selectedMood.collectAsState()

    LaunchedEffect(moodDate) {
        diaryViewModel.getMoodById(moodDate)
    }

    val emojiResId = mapOf(
        "fantastico" to R.drawable.emotionface_veryhappy,
        "feliz" to R.drawable.emotionface_happy,
        "masomenos" to R.drawable.emotionface_neutral,
        "triste" to R.drawable.emotionface_sad,
        "deprimido" to R.drawable.emotionface_verysad,
    )

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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderArrow(
                onClick = { navHostController.popBackStack() },
                title = mood?.let { formatDateForDisplay(it.moodDate) } ?: ""
            )

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = stringResource(R.string.mood_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (mood == null) {
                CircularProgressIndicator(color = Color.Gray)
            } else {
                val resourceId = emojiResId[mood!!.moodType] ?: R.drawable.emotionface_happy

                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "Emoji",
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = ComprobarMoodType(mood!!.moodType),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = mood!!.diaryEntry,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Botón en el fondo
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            CustomButton(
                onClick = {
                    navHostController.navigate("letterScreen/${mood?.moodDate}")
                },
                        buttonText = stringResource(R.string.mood_button_letter),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                backgroundColor = Green,
                textColor = White
            )


        }
    }
}




