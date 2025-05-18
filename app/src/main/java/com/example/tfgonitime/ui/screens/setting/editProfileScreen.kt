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
import androidx.compose.foundation.lazy.items // Import items for FlowRow
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
import androidx.wear.compose.material3.IconButton
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.viewmodel.AuthViewModel // Mantén AuthViewModel si lo usas para el nombre
import com.example.tfgonitime.viewmodel.LanguageViewModel
import com.example.tfgonitime.viewmodel.SettingsViewModel // Importa SettingsViewModel para la foto de perfil
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

import java.util.Locale

@Composable
fun EditProfileScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel, // Necesitamos AuthViewModel para el nombre y su lógica de guardado
    languageViewModel: LanguageViewModel,
    settingsViewModel: SettingsViewModel // Necesitamos SettingsViewModel para la foto de perfil (estado local persistente)
) {
    val context = LocalContext.current

    // Cargar el idioma al iniciar la pantalla
    LaunchedEffect(Unit) {
        languageViewModel.loadLocale(context)
        // La foto de perfil se gestiona localmente en SettingsViewModel para esta sesión
        // SettingsViewModel cargará la foto de perfil desde DataStore en su init
    }

    val locale by languageViewModel.locale

    // Observar el estado de la foto de perfil desde SettingsViewModel (la fuente de verdad local persistente)
    // SettingsViewModel carga esto desde DataStore
    val selectedProfilePictureResource by settingsViewModel.profilePictureResource.collectAsState()


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

    // Define las imágenes disponibles para la foto de perfil
    val profilePictures = listOf(
        R.drawable.head_onigiri,
        R.drawable.head_daifuku,
        R.drawable.head_taiyaki,
        R.drawable.head_takoyaki,
        R.drawable.head_coffe_jelly
    )

    var firstName by remember { mutableStateOf("") }
    // val lastName by remember { mutableStateOf("") } // Elimina si no se usa y corrige la advertencia

    // Cargar el nombre inicial del usuario cuando la pantalla se compone o el usuario cambia
    LaunchedEffect(authViewModel.userName) {
        // Recolectar el primer valor emitido por el StateFlow o Flow
        authViewModel.userName.collect { name ->
            firstName = name.orEmpty() // Usa orEmpty() para manejar el caso nulo
        }
    }


    Scaffold(
        // Consider usar MaterialTheme.colorScheme.background si quieres que respete el tema oscuro
        containerColor = Color.White, // Puedes cambiar a MaterialTheme.colorScheme.background si quieres que respete el tema
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 50.dp) // Márgenes
            ) {
                // Cabecera con el botón de volver atrás
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navHostController.popBackStack() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // Consider Icons.AutoMirrored.Filled.ArrowBack para soporte RTL
                            contentDescription = "Volver atrás",
                            // Consider usar MaterialTheme.colorScheme.onBackground o similar para respetar el tema
                            tint = Color.Black // O MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        text = stringResource(R.string.settings_edit_profile),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        // Consider usar MaterialTheme.colorScheme.onBackground o similar para respetar el tema
                        color = Color.Black // O MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.size(24.dp)) // Para balancear visualmente
                }

                // Contenido de la pantalla
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        // Mostrar la foto de perfil seleccionada (del SettingsViewModel)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(115.dp) // Tamaño del círculo
                                    .clip(CircleShape) // Recorta a círculo
                                    // Consider usar un color del tema para el fondo del círculo
                                    .background(Color.Gray) // O un color del tema como MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                // Imagen de perfil usando la ID del recurso del SettingsViewModel
                                Image(
                                    painter = painterResource(id = selectedProfilePictureResource),
                                    contentDescription = "Imagen de perfil seleccionada",
                                    modifier = Modifier
                                        .fillMaxSize()
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
                                // Consider usar MaterialTheme.colorScheme.onBackground o similar para respetar el tema
                                color = Color.Black // O MaterialTheme.colorScheme.onBackground
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
                            // Consider usar MaterialTheme.colorScheme.onBackground o similar para respetar el tema
                            color = Color.Black // O MaterialTheme.colorScheme.onBackground
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
                                        .size(60.dp) // Tamaño de cada imagen en el selector
                                        .clip(CircleShape)
                                        .border(
                                            width = if (resourceId == selectedProfilePictureResource) 3.dp else 1.dp, // Borde si está seleccionada
                                            color = if (resourceId == selectedProfilePictureResource) MaterialTheme.colorScheme.primary else Color.Gray, // Color del borde (considera un color del tema)
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            // Llama a SettingsViewModel para actualizar el estado local (guarda en DataStore)
                                            settingsViewModel.setProfilePicture(resourceId)
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = resourceId),
                                        contentDescription = "Avatar Option",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape) // Asegura que la imagen se recorte
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
                                // Consider usar MaterialTheme.colorScheme.onBackground o similar para respetar el tema
                                color = Color.Black // O MaterialTheme.colorScheme.onBackground
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
                            // Consider usar MaterialTheme.colorScheme.onBackground o similar para respetar el tema
                            color = Color.Black // O MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // ESTE ES EL CAMPO DE TEXTO PARA EL NOMBRE
                    item {
                        TextField(
                            value = firstName, // <- Usa el estado firstName
                            onValueChange = { firstName = it }, // <- Actualiza el estado cuando cambia
                            placeholder = { Text(stringResource(R.string.name_hint)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    1.dp,
                                    Color.Gray, // Consider usar un color del tema (Ej: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    shape = MaterialTheme.shapes.medium
                                ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                // Consider usar MaterialTheme.colorScheme.onSurface o similar para el texto
                                cursorColor = Color.Black // O MaterialTheme.colorScheme.onSurface
                            )
                            // Considerar TextStyle y KeyboardOptions si es necesario
                        )
                    }

                    // Botón de "Guardar"
                    item {
                        Button(
                            onClick = {
                                // Llama a la función que actualiza el nombre en Firebase
                                authViewModel.updateUserNameInProfile( // <-- Usa la función correcta para guardar en Firebase
                                    firstName, // Usa el valor actual del TextField
                                    context,
                                    onSuccess = {
                                        // Mostrar un mensaje de éxito (ej. Toast, Snackbar)
                                        Log.d("EditProfileScreen", "Name updated successfully!")
                                        // TODO: Implementar feedback visual al usuario
                                    },
                                    onError = { errorMessage ->
                                        // Mostrar un mensaje de error (ej. Toast, Snackbar, Dialog)
                                        Log.e("EditProfileScreen", "Error saving name: $errorMessage")
                                        // TODO: Implementar feedback visual al usuario
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            shape = RoundedCornerShape(8.dp)
                            // Puedes añadir colors = ButtonDefaults.buttonColors(...) si quieres personalizar
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                fontSize = 18.sp,
                                // Considerar MaterialTheme.colorScheme.onPrimary o similar para respetar el tema
                                color = Color.White // O MaterialTheme.colorScheme.onPrimary
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