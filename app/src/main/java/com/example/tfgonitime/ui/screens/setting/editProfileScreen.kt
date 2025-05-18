package com.example.tfgonitime.ui.screens.setting

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.tfgonitime.viewmodel.SettingsViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.example.tfgonitime.ui.components.HeaderArrow
import com.example.tfgonitime.ui.components.settingComp.DarkModeSwitch
import java.util.Locale

@Composable
fun EditProfileScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel, // Keep if needed for other potential logic, though locale is not used here
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current

    // Observar el estado del tema oscuro desde SettingsViewModel
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    // Cargar el idioma al iniciar la pantalla (Locale itself might be needed elsewhere)
    LaunchedEffect(Unit) {
        languageViewModel.loadLocale(context)
        // SettingsViewModel cargará la foto de perfil desde DataStore en su init
    }

    // Removed: val locale by languageViewModel.locale -> locale was unused after the removed LaunchedEffect

    // Observar el estado de la foto de perfil desde SettingsViewModel (la fuente de verdad local persistente)
    // SettingsViewModel carga esto desde DataStore
    val selectedProfilePictureResource by settingsViewModel.profilePictureResource.collectAsState()

    // Removed: selectedLanguage state and the LaunchedEffect that updated it,
    // as the comment indicated it was not used on this screen.

    // Define las imágenes disponibles para la foto de perfil
    val profilePictures = listOf(
        R.drawable.head_onigiri,
        R.drawable.head_daifuku,
        R.drawable.head_taiyaki,
        R.drawable.head_takoyaki,
        R.drawable.head_coffe_jelly
    )

    var firstName by remember { mutableStateOf("") }

    // Cargar el nombre inicial del usuario cuando la pantalla se compone o el usuario cambia
    LaunchedEffect(authViewModel.userName) {
        authViewModel.userName.collect { name ->
            firstName = name.orEmpty()
        }
    }

    Scaffold(
        // Usar MaterialTheme.colorScheme.background para respetar el tema oscuro/claro
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->

            // Contenedor principal con padding horizontal de 20.dp
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Aplicar padding del Scaffold (especialmente el de abajo)
                    // Aplicar padding horizontal de 20.dp a todo el contenido principal
                    .padding(horizontal = 20.dp)
            ) {
                // Cabecera con el componente HeaderArrow
                HeaderArrow(
                    onClick = { navHostController.popBackStack() }, // Acción para volver atrás
                    title = stringResource(R.string.settings_edit_profile), // Título de la pantalla
                    // HeaderArrow ya tiene padding superior e internamente maneja el layout
                    // El padding horizontal de 20.dp ya está en el Column padre
                )

                // Contenido de la pantalla en LazyColumn
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp)) // Espacio después de la cabecera
                    }

                    item {
                        // Mostrar la foto de perfil seleccionada (del SettingsViewModel)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp), // Padding extra alrededor del Box del avatar
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(115.dp) // Tamaño del círculo exterior
                                    .clip(CircleShape)
                                    // Usar un color del tema para el fondo del círculo
                                    .background(MaterialTheme.colorScheme.surfaceVariant) // Color de fondo del círculo
                            ) {
                                // Imagen de perfil - ajustada a un tamaño fijo más pequeño que el Box padre
                                Image(
                                    painter = painterResource(id = selectedProfilePictureResource),
                                    contentDescription = "Imagen de perfil seleccionada",
                                    modifier = Modifier
                                        .size(90.dp) // <--- ¡Ajuste de tamaño de la imagen! (Era fillMaxSize)
                                        .align(Alignment.Center) // Centrar la imagen dentro del Box padre
                                        .clip(CircleShape) // Asegura que la imagen también se recorte a círculo
                                )
                            }
                        }
                    }

                    item {
                        val selectAvatarText = stringResource(R.string.settings_select_avatar)
                        Text(
                            text = selectAvatarText,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                // Usar color del tema
                                color = MaterialTheme.colorScheme.onBackground
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
                            // Usar color del tema
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        // Selector de fotos de perfil usando FlowRow
                        FlowRow(
                            mainAxisSpacing = 16.dp, // Espacio horizontal entre imágenes
                            crossAxisSpacing = 16.dp, // Espacio vertical entre filas
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            mainAxisAlignment = FlowMainAxisAlignment.Center // Centra las imágenes
                        ) {
                            profilePictures.forEach { resourceId ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp) // Tamaño del círculo exterior en el selector
                                        .clip(CircleShape)
                                        .border(
                                            width = if (resourceId == selectedProfilePictureResource) 3.dp else 1.dp, // Borde si está seleccionada
                                            color = if (resourceId == selectedProfilePictureResource) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // Color del borde (usar colores del tema)
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            // Llama a SettingsViewModel para actualizar el estado local (guarda en DataStore)
                                            settingsViewModel.setProfilePicture(resourceId)
                                        }
                                ) {
                                    // Imagen en el selector - ajustada a fillMaxSize pero dentro del Box más pequeño
                                    // Esto funciona bien si el drawable es un icono pequeño con transparencia alrededor
                                    Image(
                                        painter = painterResource(id = resourceId),
                                        contentDescription = "Avatar Option",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape) // Asegura que la imagen se recorte
                                    )
                                }
                            }
                        }
                    }

                    // Sección de Nombre
                    item {
                        val nameText = stringResource(R.string.name_hint)
                        Text(
                            text = nameText,
                            style = TextStyle(
                                fontSize = 20.sp, // Reduje ligeramente el tamaño del texto de sección para consistencia
                                fontWeight = FontWeight.SemiBold, // Cambié a SemiBold para consistencia con SettingsScreen
                                // Usar color del tema
                                color = MaterialTheme.colorScheme.onBackground
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
                            // Usar color del tema
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        TextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            placeholder = { Text(stringResource(R.string.name_hint), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }, // Placeholder con color del tema
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // Borde con color del tema
                                    shape = MaterialTheme.shapes.medium
                                ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                // Colores del tema para el texto y cursor del TextField
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary // O onSurface
                            )
                        )
                    }



                    // Botón de "Guardar"
                    item {
                        Button(
                            onClick = {
                                // Llama a la función que actualiza el nombre en Firebase
                                // Asegúrate de que esta función updateUserNameInProfile existe en tu AuthViewModel
                                authViewModel.updateUserNameInProfile(
                                    firstName,
                                    context, // Pasa el contexto si la función lo requiere
                                    onSuccess = {
                                        Log.d("EditProfileScreen", "Name updated successfully!")
                                        // TODO: Implementar feedback visual (Toast/Snackbar)
                                    },
                                    onError = { errorMessage ->
                                        Log.e("EditProfileScreen", "Error saving name: $errorMessage")
                                        // TODO: Implementar feedback visual (Toast/Snackbar/Dialog)
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            shape = RoundedCornerShape(8.dp)
                            // Usar colores por defecto del tema o definir ButtonDefaults.buttonColors(...)
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                fontSize = 18.sp,
                                // Color por defecto del texto del botón primario suele ser onPrimary
                                color = MaterialTheme.colorScheme.onPrimary // O Color.White si prefieres forzarlo
                            )
                        }
                    }
                    // Añadir espacio al final para que el último botón no quede pegado al borde inferior
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}