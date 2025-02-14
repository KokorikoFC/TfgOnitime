package com.example.tfgonitime.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import java.util.Locale

@Composable
fun EditProfileScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel
) {

    val context = LocalContext.current

    // Cargar el idioma al iniciar la pantalla
    LaunchedEffect(Unit) {
        languageViewModel.loadLocale(context)
    }

    val locale by languageViewModel.locale

    val languages = listOf(
        "Español (España)" to Locale("es"),
        "Inglés (Reino Unido)" to Locale("en"),
        "Gallego" to Locale("gl")
    )

    // Encuentra el idioma actual
    var selectedLanguage by remember {
        mutableStateOf(
            languages.find { it.second.language == locale.language }?.first
                ?: languages[0].first
        )
    }

    // Actualizar el idioma seleccionado cuando se carga el idioma desde LanguageViewModel
    LaunchedEffect(locale) {
        selectedLanguage = languages.find { it.second.language == locale.language }?.first
            ?: languages[0].first
    }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    Scaffold(
        containerColor = Color.White,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Ajustes",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Círculo gris grande
                        Box(
                            modifier = Modifier
                                .size(115.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .align(Alignment.Center)
                        )

                        // Círculo negro
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
                item {
                    Text(
                        text = "Nombre",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                }

                item {

                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = { Text( stringResource(R.string.mood_diary_entry)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .border(
                                1.dp,
                                Color.Gray,
                                shape = MaterialTheme.shapes.medium
                            ), // Borde personalizado
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, // Sin fondo al enfocar
                            unfocusedContainerColor = Color.Transparent, // Sin fondo al desenfocar
                            focusedIndicatorColor = Color.Transparent, // Sin línea de indicador al enfocar
                            unfocusedIndicatorColor = Color.Transparent, // Sin línea de indicador al desenfocar
                            cursorColor = Color.Black, // Cursor negro
                        )
                    )
                }

                item {
                    Text(
                        text = "Apellido",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                }

                item {

                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = { Text( stringResource(R.string.mood_diary_entry)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .border(
                                1.dp,
                                Color.Gray,
                                shape = MaterialTheme.shapes.medium
                            ), // Borde personalizado
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, // Sin fondo al enfocar
                            unfocusedContainerColor = Color.Transparent, // Sin fondo al desenfocar
                            focusedIndicatorColor = Color.Transparent, // Sin línea de indicador al enfocar
                            unfocusedIndicatorColor = Color.Transparent, // Sin línea de indicador al desenfocar
                            cursorColor = Color.Black, // Cursor negro
                        )
                    )
                }
                item {
                    Text(
                        text = "Género",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 1.dp)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.dp, bottom = 3.dp),
                        thickness = 1.dp,
                        color = Color.Black
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre botones
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { /* Acción para Masculino */ },
                            modifier = Modifier.weight(1f) // Asegura que los botones se distribuyan equitativamente
                        ) {
                            Text("Masculino")
                        }

                        Button(
                            onClick = { /* Acción para Femenino */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Femenino")
                        }

                        Button(
                            onClick = { /* Acción para Otro */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Otro")
                        }
                    }
                }



            }//Aquí se cierra el LazyColum
        } // Aquí se cierra el bloque content
    ) // Aquí se cierra el Scaffold
} // Aquí se cierra la función EditProfileSettingScreen


