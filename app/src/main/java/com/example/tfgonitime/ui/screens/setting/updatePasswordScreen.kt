package com.example.tfgonitime.ui.screens.setting

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.viewmodel.AuthViewModel

// Import custom components
import com.example.tfgonitime.ui.components.CustomPasswordField
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.HeaderArrow
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White

@Composable
fun UpdatePasswordScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeaderArrow(
                onClick = { navHostController.popBackStack() },
                title = stringResource(R.string.settings_change_password)
            )

            Spacer(modifier = Modifier.height(80.dp))


            CustomPasswordField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = stringResource(R.string.update_password_current),
                placeholder = stringResource(R.string.update_password_current),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            CustomPasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = stringResource(R.string.password_hint),
                placeholder = stringResource(R.string.password_hint),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            CustomPasswordField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = stringResource(R.string.confirm_password_hint),
                placeholder = stringResource(R.string.confirm_password_hint),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    navHostController.navigate("changePasswordScreen")
                }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            CustomButton(
                onClick = {
                    if (isLoading) return@CustomButton

                    isErrorVisible = false
                    errorMessage = ""

                    if (newPassword != confirmNewPassword) {
                        errorMessage = context.getString(R.string.update_password_mismatch)
                        isErrorVisible = true
                        return@CustomButton
                    }
                    if (newPassword.length < 6) {
                        errorMessage = context.getString(R.string.update_password_too_short)
                        isErrorVisible = true
                        return@CustomButton
                    }

                    isLoading = true

                    authViewModel.updatePassword(
                        currentPassword,
                        newPassword,
                        context,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, context.getString(R.string.update_password_success), Toast.LENGTH_SHORT).show()
                            navHostController.popBackStack()
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                            isErrorVisible = true
                        }
                    )
                },
                buttonText = stringResource(R.string.save_changes),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                backgroundColor = Green, // usar color consistente con EditProfile
                textColor = White
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedMessage(
                message = errorMessage,
                isVisible = isErrorVisible,
                onDismiss = { isErrorVisible = false },
                isWhite = false
            )
        }
    }
}

