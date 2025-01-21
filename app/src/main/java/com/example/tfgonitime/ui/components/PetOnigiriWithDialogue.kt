package com.example.tfgonitime.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.DarkBrown
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.ui.theme.*
import org.w3c.dom.Text


@Composable
fun PetOnigiriWithDialogue(
    bubbleText: String,
    showBubbleText:Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .zIndex(1f).padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .fillMaxHeight(0.8f)
                .align(Alignment.BottomStart), contentAlignment = Alignment.CenterEnd
        ) {
            if(showBubbleText){
                SpeechBubbleWithText(bubbleText)
            }

        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset(y = 15.dp), // Baja la caja para superponerse
            contentAlignment = Alignment.BottomEnd // Alinea el contenido al fondo a la derecha
        ) {


            Image(
                painter = painterResource(id = R.drawable.onigiri_apoyado_scaled), // Ruta de la imagen
                contentDescription = "Muñeco apoyado",
                modifier = Modifier.size(130.dp),
                contentScale = ContentScale.Fit
            )
        }
    }

}

@Composable
fun SpeechBubbleWithText(text: String) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        // Fondo del bocadillo con esquinas redondeadas
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, White),
            modifier = Modifier.padding(end = 16.dp) // Espacio para el triángulo
        ) {
            Text(
                text = text,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.padding(16.dp) // Espaciado interno
            )
        }

        // Triángulo del bocadillo apuntando hacia la derecha
        Canvas(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterEnd) // Alinea al lado derecho del bocadillo
        ) {
            val path = Path().apply {
                moveTo(0f, 0f) // Punto superior izquierdo del triángulo
                lineTo(size.width, size.height / 2) // Punto central derecho del triángulo
                lineTo(0f, size.height) // Punto inferior izquierdo del triángulo
                close()
            }

            // Dibuja el triángulo
            drawPath(
                path = path,
                color = Color.White,
                style = Fill
            )

            // Borde del triángulo
            drawPath(
                path = path,
                color = White,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}






