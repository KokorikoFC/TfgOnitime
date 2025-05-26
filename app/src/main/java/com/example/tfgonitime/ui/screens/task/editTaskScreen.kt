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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
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
    var selectedGroupId by remember { mutableStateOf(taskToEdit.groupId) }

    var selectedDaysFullNames by remember {
        mutableStateOf(taskToEdit.reminder?.days.orEmpty().toList())
    }

    var reminderEnabled by remember { mutableStateOf(taskToEdit.reminder != null && taskToEdit.reminder.isSet) }
    var reminderTime by remember { mutableStateOf(taskToEdit.reminder?.time) }

    val groups by groupViewModel.groupsState.collectAsState()
    val loading by groupViewModel.loadingState.collectAsState()

    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    // Aquí defines los strings usando stringResource dentro del contexto composable
    val errorTitleEmpty = stringResource(id = R.string.taskTitleEmptyError)
    val errorSelectTime = stringResource(id = R.string.selectReminderTimeError)
    val errorSelectDay = stringResource(id = R.string.selectReminderDayError)
    val buttonText =
        stringResource(id = R.string.saveTaskButton) // crea este string en strings.xml como "Guardar Cambios"
    val headerTitle =
        stringResource(id = R.string.editTaskTitle) // crea este string en strings.xml como "Editar Tarea"
    val labelTaskName = stringResource(id = R.string.taskNameLabel)
    val placeholderTaskName = stringResource(id = R.string.taskNamePlaceholder)
    val labelTaskDescription = stringResource(id = R.string.taskDescriptionLabel)
    val placeholderTaskDescription = stringResource(id = R.string.taskDescriptionPlaceholder)
    val enableReminderLabel = stringResource(id = R.string.enableReminder)

    val context = LocalContext.current

    if (userId == null) return

    LaunchedEffect(userId) {
        groupViewModel.loadGroups(userId)
    }

    LaunchedEffect(groups) {
        if (taskToEdit.groupId != null && groups.isNotEmpty()) {
            val initialGroup = groups.find { it.groupId == taskToEdit.groupId }?.groupName
            selectedGroupName = initialGroup
        } else if (taskToEdit.groupId == null) {
            selectedGroupName = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                title = headerTitle
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
                        label = labelTaskName,
                        placeholder = placeholderTaskName,
                    )
                }

                item {
                    CustomTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = labelTaskDescription,
                        placeholder = placeholderTaskDescription
                    )
                }
                item {
                    DaysOfWeekSelector(
                        selectedDaysFullNames = selectedDaysFullNames,
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
                            Text(
                                enableReminderLabel,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            CustomToggleSwitch(
                                checked = reminderEnabled,
                                onCheckedChange = { reminderEnabled = it }
                            )
                        }

                        if (reminderEnabled) {
                            ReminderTimePicker(
                                selectedTime = reminderTime,
                                onTimeSelected = { timeString -> reminderTime = timeString }
                            )
                        }
                    }
                }

                item {
                    GroupSelector(
                        navHostController = navHostController,
                        groups = groups,
                        selectedGroupName = selectedGroupName,
                        selectedGroupId = selectedGroupId,
                        onGroupSelected = { groupId ->
                            selectedGroupId = groupId
                            selectedGroupName = groups.find { it.groupId == groupId }?.groupName
                        },
                        userId = userId
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                CustomButton(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = errorTitleEmpty
                            isErrorVisible = true
                            return@CustomButton
                        }

                        if (reminderEnabled) {
                            if (reminderTime.isNullOrBlank()) {
                                errorMessage = errorSelectTime
                                isErrorVisible = true
                                return@CustomButton
                            }
                            if (selectedDaysFullNames.isEmpty()) {
                                errorMessage = errorSelectDay
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
                            context = context,
                            updatedTask,
                            onSuccess = {
                                navHostController.popBackStack()
                            },
                            onError = { error ->
                                errorMessage = error
                                isErrorVisible = true
                            }
                        )
                    },
                    buttonText = buttonText,
                    modifier = Modifier.fillMaxWidth()
                )
            }

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

