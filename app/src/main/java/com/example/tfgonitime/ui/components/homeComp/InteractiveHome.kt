package com.example.tfgonitime.ui.components.homeComp

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.zIndex
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.data.model.Pets // Import Pets data class if needed

@Composable
fun InteractiveHome(
    showPet: Boolean = true,
    selectedFurnitureMap: Map<String, String>,
    furnitureCatalog: List<Furniture>,
    selectedPetImageResId: String? = null // Cambiado a String
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
                    "floor_l_slot" -> Modifier
                        .offset(x = (-40).dp, y = 90.dp)
                        .zIndex(3f)

                    "floor_r_slot" -> Modifier
                        .offset(x = 85.dp, y = 40.dp)
                        .zIndex(3f)

                    "rug" -> Modifier
                        .offset(x = 0.dp, y = 60.dp)
                        .zIndex(1f)

                    "left_l_wall" -> Modifier
                        .offset(x = (-80).dp, y = 28.dp)
                        .zIndex(1f)

                    "left_r_wall" -> Modifier
                        .offset(x = (-22).dp, y = (-69).dp)
                        .zIndex(1f)

                    "right_wall" -> Modifier
                        .offset(x = 70.dp, y = (-50).dp)
                        .zIndex(1f)

                    else -> Modifier
                        .size(60.dp)
                        .zIndex(0f)
                }


                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Furniture for $slotName",
                    modifier = furnitureModifier
                )
            }
        }

        // -------- MASCOTA DINÁMICA/CONTROLADA --------
        if (showPet) {
            if (selectedPetImageResId != null) {
                val petImageResId = selectedPetImageResId?.let { petImageName ->
                    // Convertir el nombre de la imagen en un ID de recurso
                    val context = LocalContext.current
                    context.resources.getIdentifier(petImageName, "drawable", context.packageName)
                        .takeIf { it != 0 }
                }

                // Si encontramos el recurso de la imagen, la mostramos
                petImageResId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Mascota Actual del Usuario",
                        modifier = Modifier
                            .size(80.dp)
                            .offset(y = 30.dp)
                            .zIndex(3f)
                    )
                } ?: run {
                    // Si no se encuentra la imagen, mostrar un placeholder o mensaje

                }
            }
        }
    }
}

