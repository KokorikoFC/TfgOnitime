package com.example.tfgonitime.ui.screens.task

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Reminder
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.CustomToggleSwitch
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.taskComp.DaysOfWeekSelector
import com.example.tfgonitime.ui.components.taskComp.GroupSelector
import com.example.tfgonitime.ui.components.taskComp.ReminderTimePicker
import com.example.tfgonitime.ui.theme.*
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

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGroupName by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }

    // Estado para los días seleccionados, ahora almacenará NOMBRES COMPLETOS
    var selectedDaysFullNames by remember { mutableStateOf<List<String>>(emptyList()) }

    // Estado para el recordatorio
    var reminderEnabled by remember { mutableStateOf(false) }
    // Estado para la hora del recordatorio, ahora almacenará un String "HH:mm"
    var reminderTime by remember { mutableStateOf<String?>(null) } // Cambiado de Long? a String?


    val groups by groupViewModel.groupsState.collectAsState()
    val loading by groupViewModel.loadingState.collectAsState()

    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    if (userId == null) return

    LaunchedEffect(userId) {
        groupViewModel.loadGroups(userId)
    }

    LaunchedEffect(selectedGroupName) {
        selectedGroupName?.let {
            val groupIdResult = groupViewModel.getGroupIdByName(userId, it)
            groupIdResult.onSuccess { groupId -> selectedGroupId = groupId }
            groupIdResult.onFailure {
                Log.e("AddTaskScreen", "Error obteniendo el ID del grupo: ${it.message}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        GoBackArrow(
            onClick = {
                navHostController.navigate("homeScreen") {
                    popUpTo("homeScreen") { inclusive = true }
                }
            },
            isBrown = true,
            title = "Añadir Tarea",
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 80.dp),
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
                    // Pasar la lista de nombres completos seleccionados
                    selectedDaysFullNames = selectedDaysFullNames,
                    // Recibir el nombre completo del día cuando se selecciona
                    onDaySelected = { dayFullName ->
                        selectedDaysFullNames = if (selectedDaysFullNames.contains(dayFullName)) {
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Habilitar Recordatorio", color = DarkBrown)
                        CustomToggleSwitch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it }
                        )
                    }

                    if (reminderEnabled) {
                        ReminderTimePicker(
                            // Pasar el estado del String de la hora
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
                    onGroupSelected = { selectedGroupId = it },
                    userId = userId
                )
            }

        }

        //------------------BOTÓN DE GUARDAR TAREA------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp) // Espacio para que no se corte en pantallas con barra de navegación
        ) {
            CustomButton(
                onClick = {
                    // Validar recordatorio si está habilitado
                    if (reminderEnabled) {
                        if (reminderTime.isNullOrBlank()) {
                            errorMessage = "Por favor, selecciona la hora para el recordatorio."
                            isErrorVisible = true
                            return@CustomButton // Detener si falla la validación
                        }
                        if (selectedDaysFullNames.isEmpty()) {
                            errorMessage = "Por favor, selecciona al menos un día para el recordatorio."
                            isErrorVisible = true
                            return@CustomButton // Detener si falla la validación
                        }
                    }


                    val newTask = Task(
                        // id será generado por Firestore, no lo establecemos aquí
                        title = title,
                        description = description,
                        groupId = selectedGroupId,
                        // Decide si los días de la Tarea son diferentes a los días del Recordatorio.
                        // Si no, puedes usar selectedDaysFullNames aquí también, aunque para la alarma solo se usan los del reminder.
                        days = emptyList(), // Ejemplo: la tarea no tiene días propios, solo el recordatorio
                        // O si la tarea *sí* tiene días propios (separado del recordatorio):
                        // days = selectedDaysFullNames, // Usar los días seleccionados para la tarea

                        reminder = if (reminderEnabled) {
                            // Crear el objeto Reminder SÓLO si reminderEnabled es true
                            Reminder(
                                isSet = true, // Confirmar que está activado
                                time = reminderTime, // Pasar el String "HH:mm"
                                days = selectedDaysFullNames // Pasar la lista de nombres completos
                            )
                        } else {
                            null // No hay recordatorio si no está habilitado
                        }
                    )

                    // Validar que el título no esté vacío (ya lo hace el ViewModel, pero pre-validar en UI es bueno)
                    if (newTask.title.isBlank()) {
                        errorMessage = "El título de la tarea no puede estar vacío."
                        isErrorVisible = true
                        return@CustomButton
                    }


                    taskViewModel.addTask(userId, newTask, onSuccess = {
                        // Navegar hacia atrás después de agregar con éxito
                        navHostController.popBackStack()
                    }, onError = { error ->
                        // Mostrar mensaje de error en caso de fallo
                        errorMessage = error
                        isErrorVisible = true
                    })
                },
                buttonText = "Añadir Tarea",
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