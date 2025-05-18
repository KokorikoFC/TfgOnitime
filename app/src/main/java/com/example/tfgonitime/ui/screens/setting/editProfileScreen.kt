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
import androidx.compose.material3.Scaffold // Mantener Scaffold por la bottomBar
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
import com.example.tfgonitime.ui.components.CustomBottomNavBar // Mantener la bottomBar
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import com.example.tfgonitime.viewmodel.SettingsViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.example.tfgonitime.ui.components.HeaderArrow // Asegúrate de que esta ruta sea correcta
import com.example.tfgonitime.ui.components.settingComp.DarkModeSwitch
import java.util.Locale

@Composable
fun EditProfileScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current

    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    val selectedProfilePictureResource by settingsViewModel.profilePictureResource.collectAsState()

    LaunchedEffect(Unit) {
        languageViewModel.loadLocale(context)
    }

    val profilePictures = listOf(
        R.drawable.head_onigiri,
        R.drawable.head_daifuku,
        R.drawable.head_taiyaki,
        R.drawable.head_takoyaki,
        R.drawable.head_coffe_jelly
    )

    var firstName by remember { mutableStateOf("") }

    LaunchedEffect(authViewModel.userName) {
        authViewModel.userName.collect { name ->
            firstName = name.orEmpty()
        }
    }

    Scaffold( // Mantenemos Scaffold por la bottomBar
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->

            // Contenedor principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // >>> CAMBIO AQUÍ <<<
                    // Aplicamos solo el padding inferior de Scaffold (para la bottom bar)
                    // y el padding horizontal. Removemos el padding superior automático.
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .padding(horizontal = 20.dp)
            ) {
                // Cabecera con el componente HeaderArrow
                // HeaderArrow se colocará ahora más arriba, justo debajo de la barra de estado
                HeaderArrow(
                    onClick = { navHostController.popBackStack() },
                    title = stringResource(R.string.settings_edit_profile),
                )

                // Contenido de la pantalla en LazyColumn
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // NOTA: Este Spacer ahora agrega espacio *debajo* del HeaderArrow.
                    // Si quieres que el contenido (avatar, texto, etc.) empiece justo
                    // después del HeaderArrow sin este espacio adicional, puedes eliminarlo.
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
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
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Image(
                                    painter = painterResource(id = selectedProfilePictureResource),
                                    contentDescription = "Imagen de perfil seleccionada",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .align(Alignment.Center)
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        FlowRow(
                            mainAxisSpacing = 16.dp,
                            crossAxisSpacing = 16.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            mainAxisAlignment = FlowMainAxisAlignment.Center
                        ) {
                            profilePictures.forEach { resourceId ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (resourceId == selectedProfilePictureResource) 3.dp else 1.dp,
                                            color = if (resourceId == selectedProfilePictureResource) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            settingsViewModel.setProfilePicture(resourceId)
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = resourceId),
                                        contentDescription = "Avatar Option",
                                        modifier = Modifier
                                            .size(45.dp)
                                            .align(Alignment.Center)
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
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        TextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            placeholder = { Text(stringResource(R.string.name_hint), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.medium
                                ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                authViewModel.updateUserNameInProfile(
                                    firstName,
                                    context,
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
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    // Añadir espacio al final para que el último botón no quede pegado al borde inferior
                    // Este espacio ahora se suma al padding inferior que proporciona Scaffold
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}