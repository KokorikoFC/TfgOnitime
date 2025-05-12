package com.example.tfgonitime.ui.components.taskComp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomCheckBox
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.TaskViewModel
import androidx.compose.material3.*
import com.example.tfgonitime.ui.components.DeleteConfirmationDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    userId: String,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    taskViewModel: TaskViewModel,
    index: Int,
    totalItems: Int,
    color: Color
) {
    var showPopup by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val checked = task.completed


    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable { showPopup = true },
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight(0.6f)
                        .clip(RoundedCornerShape(500.dp))
                        .background(color)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            CustomCheckBox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    taskViewModel.updateTaskCompletion(userId, task.id, isChecked)
                }

            )
        }
    }

    if (showPopup) {
        ModalBottomSheet(
            onDismissRequest = { showPopup = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { showPopup = false }
                            onEdit()
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = DarkBrown,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Editar Tarea",
                        color = DarkBrown,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { showPopup = false }
                            showDeleteConfirmation = true
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(

                        imageVector = Icons.Default.Delete, // Aquí cambiamos al ícono de papelera
                        contentDescription = "Eliminar",
                        tint = Green,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Eliminar Tarea",
                        color = Green,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }


    DeleteConfirmationDialog(
        showDialog = showDeleteConfirmation,
        onDismiss = { showDeleteConfirmation = false },
        onConfirm = onDelete
    )


    if (index != totalItems - 1) {
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            modifier = Modifier
                .height(1.dp)
                .background(DarkBrown)
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}