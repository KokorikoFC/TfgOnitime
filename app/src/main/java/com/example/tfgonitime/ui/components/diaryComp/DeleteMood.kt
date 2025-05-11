package com.example.tfgonitime.ui.components.diaryComp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.DiaryViewModel

@Composable
fun DeleteMood(
    mood: Mood,
    diaryViewModel: DiaryViewModel,
    onClose: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = White,
        shape = RoundedCornerShape(16.dp),
        text = {
            Text(
                text = "¿Estás seguro de eliminar el estado de ánimo?",
                color = DarkBrown,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = White),
                    border = BorderStroke(1.dp, Green),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar", color = Green)
                }

                Button(
                    onClick = {
                        diaryViewModel.deleteMood(mood.moodDate)
                        diaryViewModel.clearSelectedMood()
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    border = BorderStroke(1.dp, Green),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Borrar", color = White)
                }
            }
        },
        dismissButton = {}
    )
}
