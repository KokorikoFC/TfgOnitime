package com.example.tfgonitime.ui.screens.task

import android.util.Log
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
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.CustomToggleSwitch
import com.example.tfgonitime.ui.components.taskComp.DaysOfWeekSelector
import com.example.tfgonitime.ui.components.taskComp.GroupSelector
import com.example.tfgonitime.ui.components.taskComp.ReminderTimePicker
import com.example.tfgonitime.ui.theme.Brown
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

    // Mensajes de error
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Contenido principal de la pantalla
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // Reservamos espacio para el botón en la parte inferior
            ) {
                CustomTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Nombre de la Tarea",
                    placeholder = "Nombre de la Tarea",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descripción de la tarea",
                    placeholder = "Descripción de la tarea",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                //----------------Selector de días de la semana----------------
                DaysOfWeekSelector(
                    selectedDays = selectedDays,
                    onDaySelected = { day ->
                        selectedDays = if (selectedDays.contains(day)) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                //----------------Selector de Grupo----------------
                GroupSelector(
                    loading = loading,
                    groups = groups,
                    selectedGroupName = selectedGroupName,
                    onGroupSelected = { selectedGroupName = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier=Modifier.fillMaxWidth().border(1.dp, Brown)){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                            .border(1.dp, Brown),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Habilitar Recordatorio")
                        CustomToggleSwitch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it }
                        )

                    }

                    if (reminderEnabled) {
                        // Selector de hora para el recordatorio
                        ReminderTimePicker(
                            selectedTime = reminderTime,
                            onTimeSelected = { time ->
                                reminderTime = time // Guarda la hora seleccionada
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Mostrar el botón en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)  // Alineamos el botón al fondo
            ) {
                CustomButton(
                    onClick = {
                        val newTask = Task(
                            title = title,
                            description = description,
                            groupId = selectedGroupId,  // Asegúrate de que se haya asignado correctamente
                            days = selectedDays,
                            reminder = if (reminderEnabled) Reminder(
                                isSet = 1L,
                                time = reminderTime?.toString(),
                                days = selectedDays
                            ) else null
                        )
                        // Llamada al viewModel para agregar la tarea
                        taskViewModel.addTask(userId, newTask, onSuccess = {
                            // Si la tarea se agrega correctamente, volvemos atrás en la navegación
                            navHostController.popBackStack()
                        }, onError = { error ->
                            // Si ocurre un error, mostramos el mensaje de error
                            errorMessage = error
                            isErrorVisible = true  // Flag que controla la visibilidad del mensaje de error
                        })
                    },
                    buttonText = "Añadir Tarea",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Mostrar mensaje de error (si es necesario)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                AnimatedMessage(
                    message = errorMessage,
                    isVisible = isErrorVisible,
                    onDismiss = { isErrorVisible = false }
                )
            }
        }
    }
}







