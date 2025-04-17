package com.example.tfgonitime.ui.screens.letter

import androidx.navigation.NavHostController
import com.example.tfgonitime.viewmodel.DiaryViewModel
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp // Para lineHeight
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterScreen(
    navHostController: NavHostController,
    diaryViewModel: DiaryViewModel,
    moodDate: String,
) {

    // Colores (puedes definirlos donde prefieras)
    val BeigeBackground = Color(0xFFFBF8F3)
    val TextColorSoft = Color(0xFF4A4A4A)

    val mood by diaryViewModel.selectedMood.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    val userRepository = UserRepository()
    var user by remember { mutableStateOf("") }

    // Obtener el mood al iniciar la pantalla
    LaunchedEffect(moodDate) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        isLoading = true // Inicia carga
        val mood = diaryViewModel.getMoodById(moodDate)
        if (userId != null) {
            val result = userRepository.getUserName(userId)
            user = result.getOrNull() ?: ""
        }
        isLoading = false
    }

    Scaffold(
        containerColor = BeigeBackground, // Color de fondo general
        topBar = {
            TopAppBar( // Usamos TopAppBar normal para alineación izquierda por defecto
                title = { /* Sin título en la barra superior */ },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextColorSoft // Color del icono
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Fondo transparente
                    navigationIconContentColor = TextColorSoft // Asegura color del icono
                )
            )
        }
    ) { paddingValues ->

        // Comprueba si todavía está cargando o si mood es null después de cargar
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text("Cargando carta...", modifier = Modifier.padding(top = 60.dp))
            }
        } else if (mood == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontró la carta para esta fecha.", color = TextColorSoft)
            }
        } else {
            // Si no está cargando y mood no es null, muestra el contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Aplica padding del Scaffold
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    ) // Padding adicional para el contenido
            ) {
                // Saludo (Usa el nombre del destinatario del mood)

                Text(
                    text = "Querido ${user}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextColorSoft,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Cuerpo principal de la carta (de generatedLetter)
                Text(
                    text = mood!!.generatedLetter
                        ?: "Contenido no disponible.", // Usa el texto del mood
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp // Ajusta el interlineado para legibilidad
                    ),
                    color = TextColorSoft,
                    modifier = Modifier.padding(bottom = 24.dp) // Espacio después del cuerpo
                )

                // Despedida (del mood)
                Text(
                    text = "Un abrazo grande ${user}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextColorSoft,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Espaciador para empujar la fecha al fondo
                Spacer(modifier = Modifier.weight(1f))

                // Fecha (del mood, alineada a la derecha)
                Text(
                    text = moodDate ?: "", // Usa la fecha del mood
                    style = MaterialTheme.typography.bodySmall,
                    color = TextColorSoft,
                    modifier = Modifier
                        .align(Alignment.End) // Alinear al final (derecha)
                        .padding(bottom = 16.dp) // Padding inferior
                )
            }
        }
    }

}
