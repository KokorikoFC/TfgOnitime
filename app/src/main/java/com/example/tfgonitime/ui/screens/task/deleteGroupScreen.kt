package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.tfgonitime.ui.theme.*
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

    // Mapa de colores
    val colorMap = mapOf(
        "Green" to Green,
        "DarkBrown" to DarkBrown,
        "White" to White,
        "Brown" to Brown,
        "Gray" to Gray
    )


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 20.dp, bottom = 80.dp, start = 20.dp, end = 20.dp)
    ) {
        GoBackArrow(
            onClick = { navHostController.popBackStack() },
            isBrown = true,
            title = "Eliminar Grupo"
        )
        
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (groups.isNotEmpty()) {
                items(groups) { group ->
                    val groupColor = colorMap[group.groupColor] ?: DarkBrown

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, groupColor, RoundedCornerShape(8.dp))
                            .padding(start = 10.dp, end = 0.dp, top = 5.dp, bottom = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = group.groupName,
                            color = groupColor
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
            } else {
                item {
                    Text("No hay grupos disponibles", modifier = Modifier.padding(16.dp))
                }
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
