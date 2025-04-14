package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.DeleteConfirmationDialog
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DeleteGroupScreen(
    navHostController: NavHostController,
    groupViewModel: GroupViewModel,
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    val groups by groupViewModel.groupsState.collectAsState()

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var groupToDeleteId by remember { mutableStateOf<String?>(null) }

    if (userId == null) return

    LaunchedEffect(userId) {
        groupViewModel.loadGroups(userId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoBackArrow(
            onClick = { navHostController.popBackStack() },
            isBrown = true,
            title = "Eliminar Grupo"
        )

        Column(modifier = Modifier.padding(16.dp)) {
            if (groups.isNotEmpty()) {
                groups.forEach { group ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Brown)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = group.groupName,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )

                            IconButton(
                                onClick = {
                                    groupToDeleteId = group.groupId
                                    showDeleteConfirmation = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar grupo",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            } else {
                Text("No hay grupos disponibles", modifier = Modifier.padding(16.dp))
            }
        }
    }

    DeleteConfirmationDialog(
        showDialog = showDeleteConfirmation,
        onDismiss = {
            showDeleteConfirmation = false
            groupToDeleteId = null
        },
        onConfirm = {
            groupToDeleteId?.let {
                groupViewModel.deleteGroup(userId, it)
            }
            showDeleteConfirmation = false
            groupToDeleteId = null
        }
    )
}
