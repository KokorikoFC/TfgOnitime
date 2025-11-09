package com.example.tfgonitime.ui.components.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.White

@Composable
fun InventoryCard(furniture: Furniture, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageName = furniture.imageUrl

    val imageResId = remember(imageName) {
        val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        if (resId == 0) R.drawable.default_furniture
        else resId
    }

    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(White.copy(alpha = 0.9f))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Si la imagen existe, la cargamos, sino mostramos un mensaje
        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Furniture Image",
                modifier = Modifier.size(80.dp)
            )
        } else {
            Text("Imagen no encontrada", color = White)
        }

        Text(
            text = furniture.name.takeIf { it.isNotBlank() } ?: "Mueble sin nombre",
            style = TextStyle(
                fontSize = 12.sp,
                color = DarkBrown,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
