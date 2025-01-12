package com.example.tfgonitime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R


@Composable
fun GoBackArrow(
    onClick: () -> Unit,
    isBrown: Boolean
) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 25.dp)){


    if(isBrown){
        Image(
            painter = painterResource(id = R.drawable.gobackbrown), // Ruta de la imagen
            contentDescription = "Flecha volver",
            modifier = Modifier
                .size(50.dp)
                .clickable(onClick = onClick),
            contentScale = ContentScale.Fit
        )
    }else{
        Image(
            painter = painterResource(id = R.drawable.gobackwhite), // Ruta de la imagen
            contentDescription = "Flecha volver",
            modifier = Modifier
                .size(50.dp)
                .clickable(onClick = onClick),
            contentScale = ContentScale.Fit
        )
    }
}}