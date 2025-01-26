package com.example.tfgonitime.ui.screens.task

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Reminder
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.TaskGroup
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AddTaskScreen(
    navHostController: NavHostController,
    taskViewModel: TaskViewModel,
    groupViewModel: GroupViewModel,
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Estado de la tarea que se está creando
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGroupName by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }  // Para el ID del grupo
    var selectedDays by remember { mutableStateOf<List<String>>(emptyList()) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }

    // Cargar grupos desde el ViewModel
    val groups by groupViewModel.groupsState.collectAsState()
    val loading by groupViewModel.loadingState.collectAsState()  // Para mostrar si está cargando

    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Por favor inicia sesión para ver tus tareas.",
                color = Color.Red,
            )
        }
    } else {
        // Llamar al ViewModel para obtener los grupos
        LaunchedEffect(userId) {
            groupViewModel.loadGroups(userId)  // Pasamos el userId aquí
        }

        // Lógica para obtener el ID del grupo cuando se seleccione un grupo
        LaunchedEffect(selectedGroupName) {
            selectedGroupName?.let {
                val groupIdResult = groupViewModel.getGroupIdByName(userId, it)
                groupIdResult.onSuccess { groupId ->
                    selectedGroupId = groupId // Actualizar el ID del grupo seleccionado
                }
                groupIdResult.onFailure {
                    Log.e("AddTaskScreen", "Error obteniendo el ID del grupo: ${it.message}")
                }
            }
        }

        // Mostrar grupos
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción de la tarea
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de grupo
            Text(text = "Selecciona un Grupo")
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(groups) { group ->
                        GroupBox(
                            group = group,
                            isSelected = selectedGroupName == group.groupName,
                            onClick = { selectedGroupName = group.groupName }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de días
            val daysOfWeek =
                listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
            Text(text = "Días de la Semana")
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(daysOfWeek) { day ->
                    Chip(
                        text = day,
                        selected = selectedDays.contains(day),
                        onClick = {
                            selectedDays = if (selectedDays.contains(day)) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Habilitar recordatorio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = reminderEnabled,
                    onCheckedChange = { reminderEnabled = it }
                )
                Text("Habilitar Recordatorio")
            }

            if (reminderEnabled) {
                // Selector de hora para el recordatorio
                OutlinedTextField(
                    value = reminderTime?.toString() ?: "",
                    onValueChange = { reminderTime = it.toLongOrNull() },
                    label = { Text("Hora del Recordatorio (HHMM)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && selectedGroupId != null) {
                        val newTask = Task(
                            title = title,
                            description = description,
                            groupId = selectedGroupId,  // Asegúrate de que se haya asignado correctamente
                            days = selectedDays,
                            reminder = if (reminderEnabled) Reminder(isSet = 1L, time = reminderTime?.toString(), days = selectedDays) else null
                        )

                        taskViewModel.addTask(userId, newTask)
                        navHostController.popBackStack() // Volver a la pantalla anterior
                    } else {
                        Log.d("AddTaskScreen", "No group selected or title is empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir Tarea")
            }
        }
    }
}



@Composable
fun GroupBox(group: TaskGroup, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },  // Pasar solo la función regular
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.LightGray else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = group.groupName)
            Text(text = group.groupColor)
        }
    }
}




@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() },

        ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}