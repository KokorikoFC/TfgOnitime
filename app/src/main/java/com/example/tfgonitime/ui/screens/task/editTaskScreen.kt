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
import com.example.tfgonitime.ui.components.taskComp.DaysOfWeekSelector
import com.example.tfgonitime.ui.components.taskComp.GroupSelector
import com.example.tfgonitime.ui.components.taskComp.ReminderTimePicker
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

    // Inicialización del estado con los valores de la tarea a editar
    var title by remember { mutableStateOf(taskToEdit.title) }
    var description by remember { mutableStateOf(taskToEdit.description) }
    var selectedGroupName by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf(taskToEdit.groupId) }
    var selectedDays by remember { mutableStateOf(taskToEdit.days ?: emptyList()) }
    var reminderEnabled by remember { mutableStateOf(taskToEdit.reminder != null) }
    var reminderTime by remember { mutableStateOf(taskToEdit.reminder?.time?.toLongOrNull()) }

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

        LaunchedEffect(selectedGroupName) {
            selectedGroupName?.let { groupName ->
                val groupId = groups.find { it.groupName == groupName }?.groupId
                selectedGroupId = groupId
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    GoBackArrow(
                        onClick = {
                            navHostController.navigate("homeScreen") {
                                popUpTo("homeScreen") { inclusive = true }
                            }
                        },
                        isBrown = true,
                        title = "Editar Tarea"
                    )
                }

                item {
                    CustomTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Nombre de la Tarea",
                        placeholder = "Nombre de la Tarea",
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
                        selectedDays = selectedDays,
                        onDaySelected = { day ->
                            selectedDays = if (selectedDays.contains(day)) {
                                selectedDays - day
                            } else {
                                selectedDays + day
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
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
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
                                selectedTime = reminderTime,
                                onTimeSelected = { time -> reminderTime = time }
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
                item {
                    if (isErrorVisible) {
                        AnimatedMessage(
                            message = errorMessage,
                            isVisible = isErrorVisible,
                            onDismiss = { isErrorVisible = false }
                        )
                    }
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
                        val updatedTask = taskToEdit.copy(
                            title = title,
                            description = description,
                            groupId = selectedGroupId,
                            days = selectedDays,
                            reminder = if (reminderEnabled) Reminder(
                                isSet = 1L,
                                time = reminderTime?.toString(),
                                days = selectedDays
                            ) else null
                        )

                        taskViewModel.updateTask(userId, taskToEdit.id, updatedTask, onSuccess = {
                            navHostController.popBackStack()
                        }, onError = { error ->
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

