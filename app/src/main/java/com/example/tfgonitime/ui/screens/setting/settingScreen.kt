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
import com.example.tfgonitime.ui.components.CustomToggleSwitch // Import the new component
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import java.util.Locale
import android.util.Log // Make sure this import is present
import com.example.tfgonitime.ui.components.settingComp.DarkModeSwitch
import com.example.tfgonitime.viewmodel.SettingsViewModel

@Composable
fun SettingScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel,
    settingsViewModel: SettingsViewModel
) {

    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    val userName by authViewModel.userName.collectAsState()
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

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    // State for Dark Mode toggle
    var isDarkModeEnabled by remember { mutableStateOf(false) }

    // State for Notifications toggle
    var areNotificationsEnabled by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
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
                    val settingsText = stringResource(R.string.nav_settings)
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Círculo gris grande
                        Box(
                            modifier = Modifier
                                .size(115.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.emotionface_happy), // Reemplaza con la imagen que quieres mostrar
                                contentDescription = "Descripción de la imagen",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = userName.orEmpty(), // Use .orEmpty() to display an empty string if userName is null
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { navHostController.navigate("editProfileScreen") }
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val editProfileText = stringResource(R.string.settings_edit_profile)
                        Text(
                            text = editProfileText,
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    val forgotPasswordText = stringResource(R.string.forgot_password)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { /* Acción al hacer clic en "Cambiar contraseña" */ }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = forgotPasswordText)
                    }
                }

                item {
                    val preferencesText = stringResource(R.string.settings_preferences)
                    Text(
                        text = preferencesText,
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
                    val languageText = stringResource(R.string.settings_language)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navHostController.navigate("languageScreen")
                            }
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = languageText,
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }


                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { /* Acción al hacer clic en "Modo oscuro" */ }
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val darkModeText = stringResource(R.string.settings_dark_mode)
                        Text(
                            text = darkModeText,
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.weight(1f)
                                .padding(start = 16.dp) // Añade padding a la izquierda del texto
                        )
                        // Replace the default Switch with CustomToggleSwitch
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
                            .padding(8.dp) // Padding externo
                            .clickable { /* Acción al hacer clic en "Notificaciones" */ }
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val notificationsText = stringResource(R.string.settings_notifications)
                        Text(
                            text = notificationsText,
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.weight(1f)
                                .padding(start = 16.dp)
                        )
                        // Replace the default Switch with CustomToggleSwitch
                        CustomToggleSwitch(
                            checked = areNotificationsEnabled,
                            onCheckedChange = { areNotificationsEnabled = it }
                        )
                    }
                }

                item {
                    val legalInformationText = stringResource(R.string.settings_legal_information)
                    Text(
                        text = legalInformationText,
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { /* Acción al hacer clic en "Terminos y condiciones" */ }
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val termsAndConditionsText = stringResource(R.string.settings_terms_and_conditions)
                        Text(
                            text = termsAndConditionsText,
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { /* Acción al hacer clic en "Política de privacidad" */ }
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val privacyPolicyText = stringResource(R.string.settings_privacy_policy)
                        Text(
                            text = privacyPolicyText,
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                authViewModel.logout {
                                    navHostController.navigate("splashScreen") // Navega a la pantalla de inicio
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.6f) // Ajusta el tamaño del botón
                        ) {
                            val logoutText = stringResource(R.string.settings_logout)
                            Text(text = logoutText)
                        }

                        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre los botones

                        Button(
                            onClick = {
                                Log.d("SettingsScreen", "Delete Account button clicked") // Add this line
                                showDeleteConfirmationDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            val deleteAccountText = stringResource(R.string.settings_delete_account)
                            Text(text = deleteAccountText)
                        }
                    }
                }
                item {
                    Button(
                        onClick = { navHostController.navigate("editProfileScreen") },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp) // Para evitar padding extra dentro del Button
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre los elementos
                            verticalAlignment = Alignment.CenterVertically // Alinea los elementos verticalmente en el centro
                        ) {
                            // Imagen a la izquierda
                            Image(
                                painter = painterResource(id = R.drawable.head_daifuku), // Reemplaza con la imagen que desees
                                contentDescription = "Imagen izquierda",
                                modifier = Modifier.size(24.dp) // Ajusta el tamaño de la imagen
                            )

                            // Espaciador flexible para empujar el texto al centro
                            Spacer(modifier = Modifier.weight(0.75f))

                            // Texto en el centro
                            val moreInfo = stringResource(R.string.settings_about_us)
                            Text(
                                text = moreInfo,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.weight(1f) // Hace que el texto ocupe el espacio disponible

                            )

                            // Espaciador flexible para empujar el texto al centro
                            Spacer(modifier = Modifier.weight(0.75f))

                            // Imagen a la derecha
                            Image(
                                painter = painterResource(id = R.drawable.head_onigiri), // Reemplaza con la imagen que desees
                                contentDescription = "Imagen derecha",
                                modifier = Modifier.size(24.dp) // Ajusta el tamaño de la imagen
                            )
                        }
                    }
                }

            }
        }
    )
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text(stringResource(R.string.settings_confirm_delete_account_title)) },
            text = { Text(stringResource(R.string.settings_confirm_delete_account_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.deleteAccount {
                            navHostController.navigate("splashScreen")
                        }
                        showDeleteConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Optional: Style the confirm button
                ) {
                    Text(stringResource(R.string.settings_delete_account_confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmationDialog = false }) {
                    Text(stringResource(R.string.settings_delete_account_cancel))
                }
            }
        )
    }
}

@Composable
fun LanguageOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 16.sp, color = Color.Black),
            modifier = Modifier.weight(1f)
                .padding(8.dp)
        )
        CustomRadioButton(isSelected = isSelected, onClick = onClick)
    }
}