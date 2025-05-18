package com.example.tfgonitime.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.HeaderArrow // Asegúrate de que esta ruta sea correcta

@Composable
fun TermsAndConditionsScreen(
    navHostController: NavHostController
) {
    // Eliminamos Scaffold y manejamos el fondo directamente en la Columna o un Box si prefieres
    Column( // Ahora la Column es el contenedor principal
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Aplicamos el color de fondo aquí
            .padding(horizontal = 20.dp) // Mantenemos solo los márgenes laterales
    ) {
        // Cabecera con botón de volver atrás
        HeaderArrow(
            onClick = { navHostController.popBackStack() },
            title = stringResource(R.string.settings_terms_and_conditions)
        )

        Spacer(modifier = Modifier.height(24.dp)) // Espacio después de la cabecera

        // Contenido: Términos y Condiciones
        // Usamos LazyColumn para permitir el desplazamiento de texto largo
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = stringResource(R.string.terms_and_conditions_text_placeholder),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                // TODO: Reemplaza R.string.terms_and_conditions_text_placeholder con tu texto real.
            }
        }
    }
}