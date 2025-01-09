package com.example.tfgonitime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.DarkBrown

@Composable
fun PetOnigiriWithDialogue(){
    Column(
        modifier = Modifier
            .border(2.dp, DarkBrown)
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .offset(y = 15.dp) // Baja la columna para superponerse
            .zIndex(1f)
            .padding(end = 20.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Image(
            painter = painterResource(id = R.drawable.onigiri_apoyado),
            contentDescription = "Muñeco apoyado",
            modifier = Modifier
                .size(130.dp)
        )
    }
}