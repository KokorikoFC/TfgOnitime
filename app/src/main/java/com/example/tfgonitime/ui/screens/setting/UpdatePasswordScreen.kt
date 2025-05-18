package com.example.tfgonitime.ui.screens.setting

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.viewmodel.AuthViewModel

@Composable
fun UpdatePasswordScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel // Necesitamos AuthViewModel para la lógica de actualizar contraseña
) {
    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Estado para mostrar carga

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Usa el color de fondo del tema
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                IconButton(
                    onClick = { navHostController.popBackStack() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // Consider Icons.AutoMirrored.Filled.ArrowBack
                        contentDescription = "Volver atrás",
                        tint = MaterialTheme.colorScheme.onBackground // Usa el color del tema
                    )
                }

                Text(
                    text = stringResource(R.string.settings_change_password), // Reutiliza el string si aplica, o crea uno nuevo para esta pantalla
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground // Usa el color del tema
                )

                Spacer(modifier = Modifier.size(24.dp)) // Espacio para balancear
            }
        }
        // No bottom bar en esta pantalla típicamente
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.update_password_title), // Crea este string
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña Actual
            TextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text(stringResource(R.string.update_password_current)) }, // Crea este string
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.medium), // Ajusta color del borde al tema si quieres
                colors = TextFieldDefaults.colors( // Ajusta colores para que respeten el tema
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            // Campo Nueva Contraseña
            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(stringResource(R.string.update_password_new)) }, // Crea este string
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.medium), // Ajusta color del borde al tema si quieres
                colors = TextFieldDefaults.colors( // Ajusta colores para que respeten el tema
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            // Campo Confirmar Nueva Contraseña
            TextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text(stringResource(R.string.update_password_confirm)) }, // Crea este string
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.medium), // Ajusta color del borde al tema si quieres
                colors = TextFieldDefaults.colors( // Ajusta colores para que respeten el tema
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Guardar Cambios
            Button(
                onClick = {
                    if (newPassword != confirmNewPassword) {
                        Toast.makeText(context, context.getString(R.string.update_password_mismatch), Toast.LENGTH_SHORT).show() // Crea este string
                        return@Button // Salir si no coinciden
                    }
                    if (newPassword.length < 6) { // Mínimo 6 caracteres para Firebase Auth
                        Toast.makeText(context, context.getString(R.string.update_password_too_short), Toast.LENGTH_SHORT).show() // Crea este string
                        return@Button
                    }

                    isLoading = true // Indicar que la operación está en curso
                    authViewModel.updatePassword(
                        currentPassword,
                        newPassword,
                        context,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, context.getString(R.string.update_password_success), Toast.LENGTH_SHORT).show() // Crea este string
                            navHostController.popBackStack() // O navegar a otra pantalla
                        },
                        onError = { errorMessage ->
                            isLoading = false
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show() // Mostrar el mensaje de error de Firebase
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading, // Deshabilitar botón durante la carga
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary) // Indicador de carga
                } else {
                    Text(
                        text = stringResource(R.string.save_changes), // Crea este string
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto "¿Has olvidado tu contraseña?"
            Text(
                text = stringResource(R.string.forgot_password),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary, // O un color de enlace
                    textDecoration = TextDecoration.Underline // Subrayado para parecer enlace
                ),
                modifier = Modifier.clickable {
                    // Navega a la pantalla de restablecimiento de contraseña por correo
                    navHostController.navigate("changePasswordScreen") {
                        // Opcional: pop up stack para no poder volver a esta pantalla desde el olvido
                        // popUpTo(navHostController.currentDestination?.route ?: return@clickable) { inclusive = true }
                    }
                }
            )
        }
    }
}