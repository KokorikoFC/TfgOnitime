package com.example.tfgonitime.ui.screens.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.res.painterResource
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
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
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
                    val settingsText = stringResource(R.string.settings_edit_profile)
                    Text(
                        text = settingsText,
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
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,

                    ) {
                        Box(
                            modifier = Modifier
                                .size(115.dp) // Tamaño del círculo
                        ){
                            // Círculo gris grande con imagen de perfil (debe ir PRIMERO)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.emotionface_happy),
                                    contentDescription = "Imagen de perfil",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            // Círculo negro con el ícono de "plus" (debe ir DESPUÉS)
                            Box(
                                modifier = Modifier
                                    .size(30.dp) // Tamaño del botón flotante
                                    .clip(CircleShape)
                                    .background(Color.Black) // Fondo negro para hacer visible el botón
                                    .align(Alignment.BottomEnd), // Lo coloca sobre la imagen
                                //.offset(x = 5.dp, y = 5.dp), // Ajusta la posición
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.emotionface_plus),
                                    contentDescription = "Imagen para editar foto de perfil",
                                    modifier = Modifier
                                        .size(30.dp) // Tamaño de la imagen dentro del botón
                                        .clip(CircleShape)
                                )
                            }
                        }

                    }
                }



                item {
                    val nameText = stringResource(R.string.name_hint)
                    Text(
                        text = nameText,
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
                        placeholder = { Text( stringResource(R.string.name_hint)) },
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
                    val genderText = stringResource(R.string.profile_gender_text)
                    Text(
                        text = genderText,
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
                        ){
                            val genderMale = stringResource(R.string.gender_male)
                            Text(text = genderMale)
                        }

                        Button(
                            onClick = { /* Acción para Femenino */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            val genderFemale = stringResource(R.string.gender_female)
                            Text(text = genderFemale)
                        }

                        Button(
                            onClick = { /* Acción para Otro */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            val genderOther = stringResource(R.string.gender_other)
                            Text(text = genderOther)
                        }
                    }
                }



            }//Aquí se cierra el LazyColum
        } // Aquí se cierra el bloque content
    ) // Aquí se cierra el Scaffold
} // Aquí se cierra la función EditProfileSettingScreen


