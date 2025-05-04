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
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.data.model.Pets // Import Pets data class if needed

@Composable
fun InteractiveHome(
    // Add the old showPet parameter back, with a default of true
    showPet: Boolean = true,
    // Keep the new parameter for the dynamic pet image from the database
    @DrawableRes selectedPetImageResId: Int? = null, // Default to null
    selectedFurnitureMap: Map<String, String>, // slot -> furnitureId
    furnitureCatalog: List<Furniture>          // toda la lista del cat\u00E1logo
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
                    .takeIf { it != 0 } ?: R.drawable.default_furniture // Fallback

                val furnitureModifier = when (slotName) {
                    "rug" -> Modifier
                        .offset(x = 10.dp, y = 65.dp)
                        .size(100.dp)
                    "floor_l_slot" -> Modifier
                        .offset(x = (-40).dp, y = 100.dp)
                        .size(45.dp)
                    "floor_r_slot" -> Modifier
                        .offset(x = 80.dp, y = 75.dp)
                        .size(43.dp)
                    "left_l_wall" -> Modifier
                        .offset(x = (-100).dp, y = 40.dp)
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

        // -------- MASCOTA DINÁMICA/CONTROLADA --------
        // Check showPet first. If it's explicitly false, hide the pet.
        // Otherwise, use selectedPetImageResId to determine which pet to show.
        if (showPet && selectedPetImageResId != null) {
            Image(
                // Use the provided resource ID
                painter = painterResource(id = selectedPetImageResId),
                contentDescription = "Mascota Actual del Usuario", // Improved description
                modifier = Modifier
                    .size(80.dp) // Adjust size as needed (keep consistent)
                    .offset(y = 30.dp) // Adjust position as needed (keep consistent)
            )
        }
        // Optional: Display a default pet or message if showPet is true but no specific pet is selected/loaded
        else if (showPet && selectedPetImageResId == null) {
            // This case handles when showPet is true but there's no user pet selected yet or loading
            Image(
                painter = painterResource(R.drawable.coffee_jelly_body_1), // Default placeholder
                contentDescription = "No hay mascota seleccionada o cargando",
                modifier = Modifier
                    .size(80.dp) // Keep consistent size
                    .offset(y = 30.dp) // Keep consistent position
            )
        }
        // If showPet is false, the pet is hidden, so no else block is needed here for hiding.
        // The pet Image composable is simply skipped when showPet is false.
    }
}