package com.example.tfgonitime.ui.components.taskComp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.data.model.TaskGroup
import com.example.tfgonitime.ui.theme.*

@Composable
fun GroupSelector(
    loading: Boolean,
    groups: List<TaskGroup>,
    selectedGroupName: String?,
    onGroupSelected: (String) -> Unit
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


        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                groups.forEach { group ->
                    GroupBox(
                        group = group,
                        isSelected = selectedGroupName == group.groupName,
                        onClick = { onGroupSelected(group.groupName) } // Llamamos al onClick para manejar la selección
                    )
                }
            }
        }
    }
}

@Composable
fun GroupBox(group: TaskGroup, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .border(
                width = 1.dp,  // Grosor del borde
                color = if (isSelected) Green else Brown,  // Color del borde dependiendo de si está seleccionado
                shape = RoundedCornerShape(8.dp)  // Bordes redondeados
            )
            .clip(RoundedCornerShape(8.dp)),  // Aplica el redondeo de las esquinas
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Green else White,  // Color de fondo
            contentColor = if (isSelected) Color.White else Brown,  // Color del contenido (texto) dependiendo de si está seleccionado
            disabledContainerColor = White,
            disabledContentColor = Brown
        )
    ) {
        Text(
            text = group.groupName,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)  // Centra el texto dentro del Card
        )
    }
}
