package com.example.tfgonitime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Green


@Composable
fun DecorativeBottomRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(50.dp)
                .background(
                    color = Green,
                    shape = RoundedCornerShape(16.dp)
                )
        )

        Image(
            painter = painterResource(id = R.drawable.daifuku),
            contentDescription = "Muñeco apoyado",
            modifier = Modifier
                .size(30.dp)
                .rotate(25f)
        )
        Image(
            painter = painterResource(id = R.drawable.onigiri),
            contentDescription = "Muñeco apoyado",
            modifier = Modifier
                .size(30.dp)
                .rotate(-25f)
        )
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Muñeco apoyado",
            modifier = Modifier
                .size(85.dp)
                .padding(start = 10.dp, end = 10.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.taiyaki),
            contentDescription = "Muñeco apoyado",
            modifier = Modifier
                .size(30.dp)
                .rotate(-15f)
        )
        Image(
            painter = painterResource(id = R.drawable.takoyaki),
            contentDescription = "Muñeco apoyado",
            modifier = Modifier
                .size(30.dp)
                .rotate(-25f)
        )
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(50.dp)
                .background(
                    color = Green,
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}

