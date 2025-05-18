package com.example.tfgonitime.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
// Importamos Scaffold si lo necesitas para otros componentes, pero lo eliminamos como contenedor principal
// import androidx.compose.material3.Scaffold
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
fun PrivacyPolicyScreen(
    navHostController: NavHostController
) {
    // Eliminamos Scaffold y manejamos el fondo directamente en la Columna o un Box
    Column( // Ahora la Column es el contenedor principal
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Aplicamos el color de fondo aquí
            // Eliminamos .padding(paddingValues)
            .padding(horizontal = 20.dp) // Mantenemos solo los márgenes laterales
    ) {
        // Cabecera con botón de volver atrás
        HeaderArrow(
            onClick = { navHostController.popBackStack() },
            title = stringResource(R.string.settings_privacy_policy)
        )

        Spacer(modifier = Modifier.height(24.dp)) // Espacio después de la cabecera

        // Contenido: Política de Privacidad
        // Usamos LazyColumn para permitir el desplazamiento de texto largo
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = stringResource(R.string.privacy_policy_text_placeholder),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                // TODO: Reemplaza R.string.privacy_policy_text_placeholder con tu texto real.
            }
        }
    }
}

// Añade este string resource en tu archivo res/values/strings.xml (o similar para otros idiomas)
/*
<string name="privacy_policy_text_placeholder">Aquí va el texto completo de tu Política de Privacidad. Este es solo un placeholder. Debes reemplazarlo con el contenido legal real de tu aplicación.</string>
*/