package com.example.tfgonitime.ui.components.taskComp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.TaskGroup
import com.example.tfgonitime.ui.theme.*
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun GroupSelector(
    navHostController: NavHostController,
    groups: List<TaskGroup>,
    selectedGroupName: String?,  // Puede ser null si no hay grupo seleccionado
    selectedGroupId: String?,
    onGroupSelected: (String?) -> Unit,  // Función para manejar la selección
    userId: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Brown, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Text(
            text = "Grupo",
            modifier = Modifier
                .fillMaxWidth()
                .background(Green)
                .padding(8.dp),
            color = White,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                groups.forEach { group ->
                    GroupBox(
                        group = group,
                        isSelected = selectedGroupId == group.groupId,
                        onClick = { onGroupSelected(group.groupId) }
                    )
                }

                // Aquí manejamos el caso de "General" (vacío o null)
                NoGroupBox(
                    isSelected = selectedGroupId.isNullOrEmpty(),
                    onClick = {
                        onGroupSelected(null) // Establecer el grupo como null cuando seleccionas "General"
                    }
                )

                AddGroupButton(
                    navHostController = navHostController, userId = userId
                )
            }
        }
    }
}





@Composable
fun GroupBox(group: TaskGroup, isSelected: Boolean, onClick: () -> Unit) {
    val colorMap = mapOf(
        "Green" to Green,
        "DarkBrown" to DarkBrown,
        "White" to White,
        "Brown" to Brown,
        "Gray" to Gray
    )
    Card(
        modifier = Modifier
            .clickable(indication = null, // Eliminar indicación de clic
                interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = colorMap[group.groupColor] ?: DarkBrown,
                shape = RoundedCornerShape(10.dp)
            ),

        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorMap[group.groupColor] ?: DarkBrown else White,
            contentColor = if (isSelected) Color.White else colorMap[group.groupColor] ?: DarkBrown,
            disabledContainerColor = White,
            disabledContentColor = Brown
        )
    ) {
        Text(
            text = group.groupName,
            color = if (isSelected) White else colorMap[group.groupColor] ?: DarkBrown,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun NoGroupBox(isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(indication = null, // Eliminar efecto de clic
                interactionSource = remember { MutableInteractionSource() }) {
                onClick() // Actualiza el grupo a vacío cuando se selecciona
            }
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = DarkBrown, // Color fijo para "Sin Grupo"
                shape = RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkBrown else White,
            contentColor = if (isSelected) Color.White else DarkBrown
        )
    ) {
        Text(
            text = "General",
            color = if (isSelected) White else DarkBrown,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun AddGroupButton(
    navHostController: NavHostController, userId: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { navHostController.navigate("addTaskGroupScreen/$userId") }
            .border(
                width = 1.dp,
                color = DarkBrown,
                shape = RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = White,
            contentColor = DarkBrown
        )
    ) {
        Text(
            text = "Nuevo Grupo +",
            color = DarkBrown,
            textAlign = TextAlign.Center
        )
    }
}