package com.example.tfgonitime.ui.components.diaryComp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.lint.Names.Runtime.LaunchedEffect
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DeleteMood(
    mood: Mood,
    diaryViewModel: DiaryViewModel,
    authViewModel: AuthViewModel,
    onClose: () -> Unit,
    onDelete: () -> Unit,
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    AlertDialog(
        onDismissRequest = onClose,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(16.dp),
        text = {
            Text(
                text = stringResource(R.string.confirm_delete_diary),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                    border = BorderStroke(1.dp, Green),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel), color = Green)
                }

                Button(
                    onClick = {
                        if (userId != null) {
                            diaryViewModel.deleteMood(mood.moodDate, userId)
                        }
                        diaryViewModel.clearSelectedMood()
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    border = BorderStroke(1.dp, Green),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.delete), color = White)
                }
            }
        },
        dismissButton = {}
    )
}

