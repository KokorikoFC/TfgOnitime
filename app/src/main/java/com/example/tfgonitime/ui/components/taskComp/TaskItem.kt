package com.example.tfgonitime.ui.components.taskComp

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.TaskViewModel

@Composable
fun TaskItem(
    task: Task,
    navHostController: NavHostController,
    userId: String,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    taskViewModel: TaskViewModel,
    index: Int,
    totalItems: Int
) {
    var showPopup by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(task.completed) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            //.border(2.dp, DarkBrown)
            .clickable { showPopup = true }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(0.6f)
                    .clip(RoundedCornerShape(500.dp))
                    .background(Green)
            )

            Text(text = task.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))

            /*Text(
                text = if (task.completed) "Completada" else "Pendiente",
                color = if (task.completed) Color.Green else Color.Red
            )*/

            Checkbox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    checked = isChecked
                    taskViewModel.updateTaskCompletion(userId, task.id, isChecked)
                }
            )
        }
    }

    // Popup de opciones
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text(text = "Opciones") },
            text = {
                Column {
                    TextButton(onClick = { onEdit(); showPopup = false }) {
                        Text("Editar")
                    }
                    TextButton(onClick = { onDelete(); showPopup = false }) {
                        Text("Eliminar")
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


