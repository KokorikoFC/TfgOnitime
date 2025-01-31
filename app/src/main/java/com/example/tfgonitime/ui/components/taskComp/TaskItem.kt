package com.example.tfgonitime.ui.components.taskComp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.CustomCheckBox
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.TaskViewModel

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
    var checked by remember { mutableStateOf(task.completed) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable { showPopup = true },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight(0.6f)
                        .clip(RoundedCornerShape(500.dp))
                        .background(color)
                ) // Barra vertical

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
                    checked = isChecked
                    taskViewModel.updateTaskCompletion(userId, task.id, isChecked)
                },
            )
        }
    }


    // Popup de opciones
    if (showPopup) {
        AlertDialog(
            modifier = Modifier,
            containerColor = White,
            onDismissRequest = { showPopup = false },
            title = { Text(text = "Opciones") },
            text = {
                Column {
                    TextButton(onClick = { onEdit(); showPopup = false }) {
                        Text("Editar", color = DarkBrown)
                    }
                    TextButton(onClick = { onDelete(); showPopup = false }) {
                        Text("Eliminar", color = DarkBrown)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    // Divider entre tareas, excepto en la última
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



