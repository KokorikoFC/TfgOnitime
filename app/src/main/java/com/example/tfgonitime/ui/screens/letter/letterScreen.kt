package com.example.tfgonitime.ui.screens.letter

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.DecorativeBottomRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterScreen(
    navHostController: NavHostController,
    diaryViewModel: DiaryViewModel,
    moodDate: String,
) {

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

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.papel_background), // Asegúrate de tener esta imagen
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Sello decorativo
        Image(
            painter = painterResource(id = R.drawable.stamp),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .wrapContentSize(align = Alignment.TopEnd)
                .offset(x = 20.dp)
                .size(250.dp)
                .graphicsLayer(rotationZ = 15f),
            alpha = 0.3f, // más visible
            contentScale = ContentScale.Fit
        )

        Scaffold(
            containerColor = Color.Transparent, // Color de fondo general
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = TextColorSoft
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = TextColorSoft
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    item {
                        Text(
                            text = "Querido ${user},",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp // Aumentar tamaño de letra
                            ),
                            color = TextColorSoft,
                            modifier = Modifier.padding(bottom = 30.dp)
                        )
                    }

                    item {
                        // Cuerpo principal de la carta
                        Text(
                            text = mood!!.generatedLetter
                                ?: "Contenido no disponible.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 24.sp,
                                fontSize = 16.sp // Aumentar tamaño de letra
                            ),
                            color = TextColorSoft,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 20.dp),
                        )
                    }

                    item {
                        // Despedida
                        Text(
                            text = "Un abrazo grande ${user}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp // Aumentar tamaño de letra
                            ),
                            color = TextColorSoft,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp)) // Usar Spacer solo con altura
                    }

                    item {
                        // Fecha (del mood, alineada a la derecha)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = moodDate ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 16.sp // Aumentar tamaño de letra
                                ),
                                color = TextColorSoft,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }

                    item {
                        DecorativeBottomRow(
                            modifier = Modifier.align(Alignment.BottomCenter) // Alineación correcta
                        )
                    }
                }
            }

        }
    }
}
