package com.example.tfgonitime.ui.components.storeComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Gray
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White

@Composable
fun FurnitureCard(
    furniture: Furniture,
    userCoins: Int,
    userFurnitureIds: List<String>,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val isAlreadyOwned = furniture.id in userFurnitureIds
    val isAffordable = userCoins >= furniture.price

    val context = LocalContext.current
    val imageName = furniture.imageUrl

    // Usamos remember para evitar recalcular la referencia de la imagen en cada recomposición
    val imageResId = remember(imageName) {
        val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        if (resId == 0) R.drawable.default_furniture // Si no existe la imagen, usar la predeterminada (home)
        else resId
    }


    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(DarkBrown)
            .clickable(
                onClick = onClick,
                indication = if (isAlreadyOwned || !isAffordable) null else LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isAlreadyOwned) DarkBrown else if (isAffordable) White else Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Furniture Image",
                modifier = Modifier.size(80.dp)
            )

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAlreadyOwned) {
                Text("VENDIDO", color = White)
            } else {
                Text("${furniture.price}", color = White)
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


