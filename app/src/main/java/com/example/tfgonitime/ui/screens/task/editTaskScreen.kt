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
    taskToEdit: Task // La tarea que se está editando
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Inicialización del estado con los valores de la tarea a editar
    var title by remember { mutableStateOf(taskToEdit.title) }
    var description by remember { mutableStateOf(taskToEdit.description) }
    // selectedGroupName y selectedGroupId se inicializarán con los datos de la tarea
    var selectedGroupName by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf(taskToEdit.groupId) } // Inicializa con el ID de la tarea

    // **MODIFICADO:** Estado para los días seleccionados del RECORDATORIO, inicializado con los días de la tarea
    // Asegúrate de que taskToEdit.days contiene NOMBRES COMPLETOS ("Lunes", etc.) si quieres que se inicialice correctamente
    // Si taskToEdit.days contiene abreviaturas, necesitarás mapearlas a nombres completos aquí.
    var selectedDaysFullNames by remember { mutableStateOf(taskToEdit.days.orEmpty().toList()) } // Usa .orEmpty() para List<String>?

    // Estado para el recordatorio
    // **MODIFICADO:** Inicializa reminderEnabled basándose en si el Reminder existe y si isSet es true (si el modelo es Boolean)
    var reminderEnabled by remember { mutableStateOf(taskToEdit.reminder != null && taskToEdit.reminder.isSet) }
    // **MODIFICADO:** Estado para la hora del recordatorio, ahora almacenará un String "HH:mm"
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

        // **MODIFICADO:** Lanzado un efecto cuando cambian los grupos
        LaunchedEffect(groups) {
            // Busca el nombre del grupo inicial basándose en el ID de la tarea, una vez que los grupos se cargan
            Log.d("EditTaskScreen", "Groups state updated. Attempting to set initial group name for ID: ${taskToEdit.groupId}")
            if (taskToEdit.groupId != null && groups.isNotEmpty()) {
                val initialGroup = groups.find { it.groupId == taskToEdit.groupId }?.groupName
                Log.d("EditTaskScreen", "Found initial group name: ${initialGroup} for ID ${taskToEdit.groupId}")
                selectedGroupName = initialGroup
            } else if (taskToEdit.groupId == null) {
                // Si la tarea no tiene grupo, asegurar que el nombre está a null
                Log.d("EditTaskScreen", "Task has no initial group ID. Setting selectedGroupName to null.")
                selectedGroupName = null
            }
        }

        // **QUITADO:** Este LaunchedEffect ya no es necesario si el GroupSelector está manejando la actualización del selectedGroupId via onGroupSelected
        /*
        LaunchedEffect(selectedGroupName) {
            selectedGroupName?.let { groupName ->
                val groupId = groups.find { it.groupName == groupName }?.groupId
                selectedGroupId = groupId
            }
        }
        */


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
                title = "Editar Tarea"
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
                    // **MODIFICADO:** Usar la versión del Selector que maneja nombres completos
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
                            // **MODIFICADO:** Usar la versión del TimePicker que maneja String "HH:mm"
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
                    // **REVERTIDO:** Llamada al GroupSelector como estaba originalmente
                    // **NOTA:** Tu GroupSelector debe estar manejando internamente la inicialización
                    // basándose en los valores 'selectedGroupName' y/o 'selectedGroupId' que le pasas.
                    GroupSelector(
                        navHostController = navHostController,
                        groups = groups,
                        selectedGroupName = selectedGroupName, // Pasar el nombre (ahora inicializado)
                        selectedGroupId = selectedGroupId, // Pasar el ID (ahora inicializado)
                        onGroupSelected = { groupId ->
                            // Este callback es activado por el GroupSelector cuando el usuario hace una selección
                            selectedGroupId = groupId // Actualiza el estado de ID en la pantalla
                            // Opcional: actualiza el estado de nombre si GroupSelector no devuelve nombre
                            selectedGroupName = groups.find { it.groupId == groupId }?.groupName
                        },
                        userId = userId
                        // **REVERTIDO:** Eliminada la línea que causaba el error
                        // initialSelectedGroupId = taskToEdit.groupId // <-- ESTA LÍNEA YA NO ESTÁ AQUÍ
                    )
                }
                // Ya tienes un AnimatedMessage al final del Box, este puede eliminarse
                /*
                item {
                    if (isErrorVisible) {
                        AnimatedMessage(
                            message = errorMessage,
                            isVisible = isErrorVisible,
                            onDismiss = { isErrorVisible = false },
                            isWhite = false
                        )
                    }
                }
                */
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

                        // **MODIFICADO:** Validar recordatorio si está habilitado
                        if (reminderEnabled) {
                            if (reminderTime.isNullOrBlank()) {
                                errorMessage = "Por favor, selecciona la hora para el recordatorio."
                                isErrorVisible = true
                                return@CustomButton // Detener si falla la validación
                            }
                            // **MODIFICADO:** Usar selectedDaysFullNames para la validación
                            if (selectedDaysFullNames.isEmpty()) {
                                errorMessage = "Por favor, selecciona al menos un día para el recordatorio."
                                isErrorVisible = true
                                return@CustomButton // Detener si falla la validación
                            }
                        }

                        // **MODIFICADO:** Crear el objeto Task actualizado con los datos correctos
                        val updatedTask = taskToEdit.copy(
                            title = title,
                            description = description,
                            groupId = selectedGroupId,
                            // Decide si los días de la Tarea principal se editan aquí. Si es así, usa selectedDaysFullNames.
                            // Si no, mantén taskToEdit.days (asegúrate de que estén en el formato correcto si se usan en otro lugar)
                            days = selectedDaysFullNames, // Asumiendo que los días de la tarea se editan aquí también

                            reminder = if (reminderEnabled) {
                                // Crear el objeto Reminder SÓLO si reminderEnabled es true
                                // **MODIFICADO:** Usar el tipo Boolean para isSet (si actualizaste el modelo)
                                Reminder(
                                    isSet = true, // Usar true si el modelo es Boolean
                                    time = reminderTime, // Pasar el String "HH:mm"
                                    days = selectedDaysFullNames // Pasar la lista de nombres completos
                                )
                            } else {
                                null // Si el recordatorio se deshabilita, pasamos null
                            }
                        )

                        // Llamar al ViewModel para actualizar la tarea
                        taskViewModel.updateTask(userId, taskToEdit.id, updatedTask, onSuccess = {
                            // Navegar hacia atrás después de la actualización con éxito
                            navHostController.popBackStack()
                        }, onError = { error ->
                            // Mostrar mensaje de error en caso de fallo
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