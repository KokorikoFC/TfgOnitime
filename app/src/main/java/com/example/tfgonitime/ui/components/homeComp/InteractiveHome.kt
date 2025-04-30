package com.example.tfgonitime.ui.components.homeComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R

@Composable
fun InteractiveHome(showPet: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        // -------- CASA COMO FONDO --------
        Image(
            painter = painterResource(R.drawable.home),
            contentDescription = "Casa de la mascota",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        // -------- MASCOTA (opcional) --------
        if (showPet) {
            Image(
                painter = painterResource(R.drawable.taiyaki_body_1),
                contentDescription = "Mascota",
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = 30.dp)
            )
        }
    }
}
