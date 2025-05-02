package com.example.tfgonitime.ui.components.homeComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Furniture

@Composable
fun InteractiveHome(
    showPet: Boolean,
    selectedFurnitureMap: Map<String, String>, // slot -> furnitureId
    furnitureCatalog: List<Furniture>          // toda la lista del catálogo
) {
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

        // -------- MUEBLES --------
        selectedFurnitureMap.forEach { (slotName, furnitureId) ->
            val furniture = furnitureCatalog.find { it.id == furnitureId }
            if (furniture != null) {
                val context = LocalContext.current
                val resId = context.resources.getIdentifier(
                    furniture.imageUrl,
                    "drawable",
                    context.packageName
                )
                    .takeIf { it != 0 } ?: R.drawable.default_furniture

                val furnitureModifier = when (slotName) {
                    "rug" -> Modifier
                        .offset(x = 10.dp, y = 65.dp)
                        .size(100.dp)
                    "floor_l_slot" -> Modifier
                        .offset(x = (-40).dp, y = 100.dp)
                        .size(45.dp)
                    "floor_r_slot" -> Modifier
                        .offset(x = 85.dp, y = 80.dp)
                        .size(43.dp)
                    "left_l_wall" -> Modifier
                        .offset(x = (-100).dp, y = 45.dp)
                        .size(70.dp)
                    "left_r_wall" -> Modifier
                        .offset(x = (-20).dp, y = (-60).dp)
                        .size(48.dp)
                    "right_wall" -> Modifier
                        .offset(x = 50.dp, y = (-40).dp)
                        .size(48.dp)
                    else -> Modifier.size(60.dp)
                }

                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Furniture for $slotName",
                    modifier = furnitureModifier
                )

            }
        }

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

