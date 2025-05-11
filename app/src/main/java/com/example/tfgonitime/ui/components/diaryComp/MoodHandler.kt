package com.example.tfgonitime.ui.components.diaryComp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.theme.*

import androidx.compose.material3.ExperimentalMaterial3Api

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHandler(
    mood: Mood,
    navHostController: NavHostController,
    onEliminarClick: () -> Unit,
    onClose: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { onClose() },
        sheetState = sheetState,
        containerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            // Ver estado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion {
                                onClose()
                                navHostController.navigate("moodScreen/${mood.moodDate}") {
                                    launchSingleTop = true
                                }
                            }
                    }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.navbar_icon_diary),
                    contentDescription = stringResource(R.string.mood_handler_open),
                    tint = DarkBrown,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.mood_handler_open),
                    color = DarkBrown,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Editar estado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion {
                                onClose()
                                navHostController.navigate("moodEditScreen/${mood.moodDate}") {
                                    launchSingleTop = true
                                }
                            }
                    }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.mood_handler_edit),
                    tint = DarkBrown,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.mood_handler_edit),
                    color = DarkBrown,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Eliminar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion {
                                onClose()
                                onEliminarClick()
                            }
                    }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.mood_handler_delete),
                    tint = Green,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.mood_handler_delete),
                    color = Green,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}




