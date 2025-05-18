package com.example.tfgonitime.ui.screens.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.repository.LanguageManager
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.CustomRadioButton
import com.example.tfgonitime.ui.components.CustomToggleSwitch
import com.example.tfgonitime.viewmodel.AuthViewModel // Necesitamos AuthViewModel para el nombre, cerrar sesión, eliminar cuenta, etc.
import com.example.tfgonitime.viewmodel.LanguageViewModel
import java.util.Locale
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
// Importa AutoMirrored si vas a corregir la advertencia de Icons.Filled.KeyboardArrowRight
// import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.example.tfgonitime.ui.components.DeleteConfirmationDialog
import com.example.tfgonitime.ui.components.settingComp.DarkModeSwitch
import com.example.tfgonitime.ui.theme.Brown // Asegúrate de que estos colores existan
import com.example.tfgonitime.ui.theme.Green // Asegúrate de que estos colores existan
import com.example.tfgonitime.ui.theme.Red // Asegúrate de que estos colores existan
import com.example.tfgonitime.ui.theme.White // Asegúrate de que estos colores existan
import com.example.tfgonitime.viewmodel.SettingsViewModel // Importa SettingsViewModel para la foto de perfil y tema oscuro


@Composable
fun SettingScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel, // Necesitamos AuthViewModel para el nombre, cerrar sesión, eliminar cuenta, etc.
    languageViewModel: LanguageViewModel,
    settingsViewModel: SettingsViewModel // Necesitamos SettingsViewModel para la foto de perfil (estado local persistente) y tema oscuro
) {

    // Observar el estado del tema oscuro desde SettingsViewModel
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    // Observar el estado de la foto de perfil desde SettingsViewModel (la fuente de verdad local persistente)
    // SettingsViewModel carga esto desde DataStore en su init
    val selectedProfilePictureResource by settingsViewModel.profilePictureResource.collectAsState()


    // Observar el nombre de usuario desde AuthViewModel
    val userName by authViewModel.userName.collectAsState()
    val context = LocalContext.current

    // Cargar el idioma al iniciar la pantalla
    LaunchedEffect(Unit) {
        languageViewModel.loadLocale(context)
        // SettingsViewModel cargará la foto de perfil desde DataStore en su init
    }

    val locale by languageViewModel.locale

    // Esto parece no usarse en SettingScreen, solo en LanguageScreen.
    // Puedes mantenerlo o eliminarlo si no lo necesitas aquí.
    val languages = listOf(
        "Español (España)" to Locale("es"),
        "Inglés (Reino Unido)" to Locale("en"),
        "Gallego" to Locale("gl")
    )

    // Encuentra el idioma actual
    // Esto parece no usarse en SettingScreen, solo en LanguageScreen.
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

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    // State for Notifications toggle (si gestionas esto localmente aquí o en otro ViewModel)
    var areNotificationsEnabled by remember { mutableStateOf(false) }


    Scaffold(
        // El color de fondo respetará el tema oscuro/claro si usas MaterialTheme.colorScheme.background
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    val settingsText = stringResource(R.string.nav_settings)
                    Text(
                        text = settingsText,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            // El color del texto respetará el tema oscuro/claro si usas MaterialTheme.colorScheme.onPrimary
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Círculo grande con la foto de perfil (del SettingsViewModel)
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                // Consider usar un color del tema para el fondo del círculo
                                .background(Green.copy(alpha = 0.5f)), // O el color de fondo que desees
                            contentAlignment = Alignment.Center
                        ) {
                            // Usar la imagen de perfil del SettingsViewModel
                            Image(
                                painter = painterResource(id = selectedProfilePictureResource),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(75.dp) // Ajusta el tamaño de la imagen dentro del círculo si es necesario
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = userName.orEmpty(), // Use .orEmpty() to display an empty string if userName is null
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                // El color del texto respetará el tema oscuro/claro si usas MaterialTheme.colorScheme.onPrimary
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    val profileText = stringResource(R.string.settings_profile)
                    Text(
                        text = profileText,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 4.dp),
                        thickness = 2.dp,
                        color = Green // Consider usar un color del tema o onPrimary
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navHostController.navigate("editProfileScreen") }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val editProfileText = stringResource(R.string.settings_edit_profile)
                        Text(
                            text = editProfileText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight, // Consider Icons.AutoMirrored.Filled.KeyboardArrowRight
                            contentDescription = "Ir",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Este clickable debería ir a una pantalla para cambiar la contraseña
                            .clickable { navHostController.navigate("updatePasswordScreen") } // <-- ¡CAMBIA ESTA LÍNEA!
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val forgotPasswordText = stringResource(R.string.settings_change_password)
                        Text(
                            text = forgotPasswordText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight, // Consider Icons.AutoMirrored.Filled.KeyboardArrowRight
                            contentDescription = "Ir",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    val preferencesText = stringResource(R.string.settings_preferences)
                    Text(
                        text = preferencesText,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 4.dp),
                        thickness = 2.dp,
                        color = Green // Consider usar un color del tema o onPrimary
                    )
                }

                item {
                    val languageText = stringResource(R.string.settings_language)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navHostController.navigate("languageScreen") }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = languageText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight, // Consider Icons.AutoMirrored.Filled.KeyboardArrowRight
                            contentDescription = "Ir",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val darkModeText = stringResource(R.string.settings_dark_mode)
                        Text(
                            text = darkModeText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        // DarkModeSwitch observa isDarkTheme de SettingsViewModel y llama a toggleDarkTheme
                        DarkModeSwitch(
                            isDarkTheme = isDarkTheme,
                            onCheckedChange = { settingsViewModel.toggleDarkTheme(it) }
                        )
                    }
                }


                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val notificationsText = stringResource(R.string.settings_notifications)
                        Text(
                            text = notificationsText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        // CustomToggleSwitch para notificaciones (si gestionas esto localmente o en otro ViewModel)
                        CustomToggleSwitch(
                            checked = areNotificationsEnabled,
                            onCheckedChange = { areNotificationsEnabled = it }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    val legalInformationText = stringResource(R.string.settings_legal_information)
                    Text(
                        text = legalInformationText,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 4.dp),
                        thickness = 2.dp,
                        color = Green // Consider usar un color del tema o onPrimary
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navHostController.navigate("termsAndConditionsScreen") }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val termsAndConditionsText =
                            stringResource(R.string.settings_terms_and_conditions)
                        Text(
                            text = termsAndConditionsText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight, // Consider Icons.AutoMirrored.Filled.KeyboardArrowRight
                            contentDescription = "Ir",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navHostController.navigate("privacyPolicyScreen") }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val privacyPolicyText = stringResource(R.string.settings_privacy_policy)
                        Text(
                            text = privacyPolicyText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight, // Consider Icons.AutoMirrored.Filled.KeyboardArrowRight
                            contentDescription = "Ir",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }


                item {
                    val accountText = stringResource(R.string.settings_account) // Usar settings_account
                    Text(
                        text = accountText,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 4.dp),
                        thickness = 2.dp,
                        color = Green // Consider usar un color del tema o onPrimary
                    )
                }

                // Item "Cerrar sesión"
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                authViewModel.logout {
                                    navHostController.navigate("splashScreen") {
                                        // Opcional: Configurar popUpTo para limpiar la pila de navegación
                                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val logoutText = stringResource(R.string.settings_logout)
                        Text(
                            text = logoutText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Red // Consider usar un color de error del tema
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Item "Eliminar cuenta"
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteConfirmationDialog = true }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val deleteAccountText = stringResource(R.string.settings_delete_account)
                        Text(
                            text = deleteAccountText,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Red // Consider usar un color de error del tema
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Sección "Conócenos mejor"

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brown) // O el color de fondo que desees (considera un color del tema)
                            .clickable { /* Define la navegación para "Sobre nosotros" si hay una pantalla AboutUsScreen */ }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.head_onigiri), // Reemplaza con tus drawables
                                contentDescription = "Imagen izquierda",
                                modifier = Modifier.size(34.dp)
                            )

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                val moreInfo = stringResource(R.string.settings_about_us)
                                Text(
                                    text = moreInfo,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        // Asegúrate de que el color sea legible en el fondo (Brown)
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }

                            Image(
                                painter = painterResource(id = R.drawable.daifuku_body_2), // Reemplaza con tus drawables
                                contentDescription = "Imagen derecha",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                }
                item {
                    Spacer(modifier = Modifier.height(26.dp))
                }


            }
        }
    )

    // Diálogo de confirmación para eliminar cuenta
    if (showDeleteConfirmationDialog) {
        DeleteConfirmationDialog(
            showDialog = showDeleteConfirmationDialog,
            onDismiss = { showDeleteConfirmationDialog = false },
            onConfirm = {
                // Llama a la función de AuthViewModel para eliminar la cuenta
                authViewModel.deleteAccount {
                    // Navega a la pantalla de inicio después de eliminar la cuenta
                    navHostController.navigate("splashScreen") {
                        // Opcional: Limpiar la pila de navegación
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                    }
                }
                showDeleteConfirmationDialog = false // Cerrar el diálogo después de confirmar
            }
        )
    }

}