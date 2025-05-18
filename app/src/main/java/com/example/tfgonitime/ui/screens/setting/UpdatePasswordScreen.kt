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

// Import custom components
import com.example.tfgonitime.ui.components.CustomPasswordField
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
// Import the HeaderArrow component
import com.example.tfgonitime.ui.components.HeaderArrow // Make sure this path is correct

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


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // We set topBar to an empty lambda because HeaderArrow will be part of the content
        topBar = { }
    ) { paddingValues ->
        // Use a Box to allow overlaying the loading indicator and the error message
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Apply padding from Scaffold here
                .padding(paddingValues)
        ) {
            Column(
                // Column takes the remaining space after padding
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp), // Add horizontal padding within the column
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp) // Adjust spacing as needed
            ) {
                // --- Use the HeaderArrow component here ---
                HeaderArrow(
                    onClick = { navHostController.popBackStack() },
                    title = stringResource(R.string.settings_change_password) // Use the screen title string
                    // HeaderArrow might have its own modifier parameters if needed
                )

                // Remove the initial Spacer here, HeaderArrow likely provides vertical space



                Spacer(modifier = Modifier.height(16.dp)) // Space after the main title

                CustomPasswordField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = stringResource(R.string.update_password_current),
                    placeholder = stringResource(R.string.update_password_current),
                    modifier = Modifier.fillMaxWidth()
                )

                CustomPasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = stringResource(R.string.password_hint), // Reused string
                    placeholder = stringResource(R.string.password_hint), // Reused string
                    modifier = Modifier.fillMaxWidth()
                )

                CustomPasswordField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    label = stringResource(R.string.confirm_password_hint), // Reused string
                    placeholder = stringResource(R.string.confirm_password_hint), // Reused string
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomButton(
                    onClick = {
                        if (isLoading) return@CustomButton // Prevent clicks while loading

                        isErrorVisible = false
                        errorMessage = ""

                        if (newPassword != confirmNewPassword) {
                            errorMessage = context.getString(R.string.password_mismatch)
                            isErrorVisible = true
                            return@CustomButton
                        }
                        if (newPassword.length < 6) {
                            errorMessage = context.getString(R.string.password_too_short)
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
                                Log.e("UpdatePasswordScreen", "Password update failed: $error")
                            }
                        )
                    },
                    buttonText = stringResource(R.string.save_changes),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.forgot_password),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        navHostController.navigate("changePasswordScreen") {
                            // popUpTo(navHostController.currentDestination?.route ?: return@clickable) { inclusive = true }
                        }
                    }
                )

                // Optional spacer to push content up if needed
                // Spacer(modifier = Modifier.weight(1f))
            }

            // Animated Error Message - Aligned to the bottom within the parent Box
            if (errorMessage.isNotEmpty() && isErrorVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 24.dp, end = 24.dp), // Match horizontal padding of column
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AnimatedMessage(
                        message = errorMessage,
                        isVisible = isErrorVisible,
                        onDismiss = { isErrorVisible = false },
                        isWhite = false
                    )
                }
            }

            // Loading Overlay - Centered within the parent Box
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
        }
    }
}