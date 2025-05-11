package com.example.tfgonitime.ui.components.missionComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mission
import com.example.tfgonitime.ui.components.CustomCheckBox
import com.example.tfgonitime.ui.theme.*

@Composable
fun MissionItem(
    mission: Mission,
    onComplete: (Mission) -> Unit,
    onClaimReward: (Mission) -> Unit
) {
    val missionState by rememberUpdatedState(mission) // Mantener el estado actualizado
    val isChecked = missionState.isCompleted
    val isClaimed = missionState.isClaimed

    val backgroundColor = when {
        isClaimed -> Green.copy(alpha = 0.8f)
        isChecked -> Green.copy(alpha = 0.8f)
        else -> Green.copy(alpha = 0.4f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.Center
        ) {
            val mainImage =
                if (isChecked) R.drawable.emotionface_veryhappy else R.drawable.emotionface_happy
            Image(
                painter = painterResource(id = mainImage),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            if (isClaimed) {
                Image(
                    painter = painterResource(id = R.drawable.like_hand),
                    contentDescription = "Misión Reclamada",
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = missionState.description,
                fontWeight = FontWeight.Medium,
                style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier.fillMaxWidth()

            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${missionState.reward} monedas",
                    style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isChecked) "Completada" else "No completada",
                style = TextStyle(fontSize = 12.sp, color =  MaterialTheme.colorScheme.tertiary)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        CustomCheckBox(
            checked = isClaimed,
            onCheckedChange = { claimed ->
                if (isChecked && !isClaimed && claimed) {
                    onClaimReward(missionState)
                }
            },
            enabled = isChecked && !isClaimed
        )
    }
}