package com.example.tfgonitime.ui.screens.task

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Reminder
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.CustomToggleSwitch
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.HeaderArrow
// Asegúrate de que estos imports usen las versiones modificadas de los composables
import com.example.tfgonitime.ui.components.taskComp.DaysOfWeekSelector // Usará la versión que maneja nombres completos
import com.example.tfgonitime.ui.components.taskComp.GroupSelector // <-- Usa tu versión existente
import com.example.tfgonitime.ui.components.taskComp.ReminderTimePicker // Usará la versión que maneja String "HH:mm"

import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EditTaskScreen(
    navHostController: NavHostController,
    taskViewModel: TaskViewModel,
    groupViewModel: GroupViewModel,
    taskToEdit: Task
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    var title by remember { mutableStateOf(taskToEdit.title) }
    var description by remember { mutableStateOf(taskToEdit.description) }
    var selectedGroupName by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf(taskToEdit.groupId) } // Inicializa con el ID de la tarea

    var selectedDaysFullNames by remember {
        mutableStateOf(
            taskToEdit.reminder?.days.orEmpty().toList()
        )
    }


    // Estado para el recordatorio
    // Inicializa reminderEnabled basándose en si el Reminder existe y si isSet es true (si el modelo es Boolean)
    var reminderEnabled by remember { mutableStateOf(taskToEdit.reminder != null && taskToEdit.reminder.isSet) }    // Estado para la hora del recordatorio, ahora almacenará un String "HH:mm"
    // Inicializado directamente desde el Reminder existente. Si es null, el picker mostrará la hora por defecto.
    var reminderTime by remember { mutableStateOf(taskToEdit.reminder?.time) }


    val groups by groupViewModel.groupsState.collectAsState()
    val loading by groupViewModel.loadingState.collectAsState()

    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    if (userId == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Por favor inicia sesión para ver tus tareas.",
                color = Color.Red,
            )
        }
    } else {
        LaunchedEffect(userId) {
            groupViewModel.loadGroups(userId)
        }

        LaunchedEffect(groups) {
            // Busca el nombre del grupo inicial basándose en el ID de la tarea, una vez que los grupos se cargan
            Log.d(
                "EditTaskScreen",
                "Groups state updated. Attempting to set initial group name for ID: ${taskToEdit.groupId}"
            )
            if (taskToEdit.groupId != null && groups.isNotEmpty()) {
                val initialGroup = groups.find { it.groupId == taskToEdit.groupId }?.groupName
                Log.d(
                    "EditTaskScreen",
                    "Found initial group name: ${initialGroup} for ID ${taskToEdit.groupId}"
                )
                selectedGroupName = initialGroup
            } else if (taskToEdit.groupId == null) {
                // Si la tarea no tiene grupo, asegurar que el nombre está a null
                Log.d(
                    "EditTaskScreen",
                    "Task has no initial group ID. Setting selectedGroupName to null."
                )
                selectedGroupName = null
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                HeaderArrow(
                    onClick = {
                        navHostController.navigate("homeScreen") {
                            popUpTo("homeScreen") { inclusive = true }
                        }
                    },
                    title = "Editar Tarea"
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        CustomTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Nombre de la tarea",
                            placeholder = "Nombre de la tarea",
                        )
                    }

                    item {
                        CustomTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Descripción de la tarea",
                            placeholder = "Descripción de la tarea"
                        )
                    }
                    //-----------------SELECCIONADOR DE DÍAS DE LA SEMANA-----------------
                    item {
                        DaysOfWeekSelector(
                            // Pasar la lista de nombres completos seleccionados (ahora inicializada desde reminder.days)
                            selectedDaysFullNames = selectedDaysFullNames,
                            // Recibir el nombre completo del día cuando se selecciona
                            onDaySelected = { dayFullName ->
                                selectedDaysFullNames =
                                    if (selectedDaysFullNames.contains(dayFullName)) {
                                        selectedDaysFullNames - dayFullName
                                    } else {
                                        selectedDaysFullNames + dayFullName
                                    }
                            }
                        )
                    }

                    //-----------------RECORDATORIO-----------------
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Brown, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Habilitar Recordatorio", color = MaterialTheme.colorScheme.onBackground)
                                CustomToggleSwitch(
                                    checked = reminderEnabled,
                                    onCheckedChange = { reminderEnabled = it }
                                )
                            }

                            if (reminderEnabled) {
                                // Usar la versión del TimePicker que maneja String "HH:mm"
                                ReminderTimePicker(
                                    selectedTime = reminderTime,
                                    // Recibir la hora como String "HH:mm"
                                    onTimeSelected = { timeString -> reminderTime = timeString }
                                )
                            }
                        }
                    }

                    //-----------------SELECTOR DE GRUPO-----------------
                    item {

                        GroupSelector(
                            navHostController = navHostController,
                            groups = groups,
                            selectedGroupName = selectedGroupName,
                            selectedGroupId = selectedGroupId,
                            onGroupSelected = { groupId ->
                                // Este callback es activado por el GroupSelector cuando el usuario hace una selección
                                selectedGroupId =
                                    groupId // Actualiza el estado de ID en la pantalla
                                selectedGroupName = groups.find { it.groupId == groupId }?.groupName
                            },
                            userId = userId
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }


                //------------------BOTÓN DE GUARDAR CAMBIOS------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp) // Espacio para que no se corte en pantallas con barra de navegación
                ) {
                    CustomButton(
                        onClick = {
                            // Validar título
                            if (title.isBlank()) {
                                errorMessage = "El título de la tarea no puede estar vacío."
                                isErrorVisible = true
                                return@CustomButton
                            }

                            // Validar recordatorio si está habilitado
                            if (reminderEnabled) {
                                if (reminderTime.isNullOrBlank()) {
                                    errorMessage =
                                        "Por favor, selecciona la hora para el recordatorio."
                                    isErrorVisible = true
                                    return@CustomButton
                                }
                                if (selectedDaysFullNames.isEmpty()) {
                                    errorMessage =
                                        "Por favor, selecciona al menos un día para el recordatorio."
                                    isErrorVisible = true
                                    return@CustomButton
                                }
                            }

                            val updatedTask = taskToEdit.copy(
                                title = title,
                                description = description,
                                groupId = selectedGroupId,


                                reminder = if (reminderEnabled) {

                                    Reminder(
                                        isSet = true,
                                        time = reminderTime,
                                        days = selectedDaysFullNames
                                    )
                                } else {
                                    null
                                }
                            )

                            taskViewModel.updateTask(
                                userId,
                                taskToEdit.id,
                                updatedTask,
                                onSuccess = {
                                    navHostController.popBackStack()
                                },
                                onError = { error ->
                                    errorMessage = error
                                    isErrorVisible = true
                                })
                        },
                        buttonText = "Guardar Cambios",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                //------------MENSAJE DE ERROR------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AnimatedMessage(
                        message = errorMessage,
                        isVisible = isErrorVisible,
                        onDismiss = { isErrorVisible = false },
                        isWhite = false
                    )
                }
            }
        }
    }
}