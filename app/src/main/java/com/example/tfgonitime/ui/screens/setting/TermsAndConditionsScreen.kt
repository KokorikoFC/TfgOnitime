package com.example.tfgonitime.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.HeaderArrow // Asegúrate de que esta ruta sea correcta

@Composable
fun TermsAndConditionsScreen(
    navHostController: NavHostController
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // Usar el color de fondo del tema
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Márgenes laterales
        ) {
            // Cabecera con botón de volver atrás
            HeaderArrow(
                onClick = { navHostController.popBackStack() },
                title = stringResource(R.string.settings_terms_and_conditions) // Título usando string resource
            )

            Spacer(modifier = Modifier.height(24.dp)) // Espacio después de la cabecera

            // Contenido: Términos y Condiciones
            // Usamos LazyColumn para permitir el desplazamiento de texto largo
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = stringResource(R.string.terms_and_conditions_text_placeholder), // Placeholder para el texto
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary // Usar el color de texto del tema
                    )
                    // TODO: Reemplaza R.string.terms_and_conditions_text_placeholder con tu texto real.
                    // Puedes poner el texto completo en un recurso de string o cargarlo de otra fuente.
                }
            }
        }
    }
}
