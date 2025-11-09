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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
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
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.HeaderArrow
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.ui.theme.White
import com.example.tfgonitime.viewmodel.AuthViewModel // Mantén AuthViewModel si lo usas para el nombre
import com.example.tfgonitime.viewmodel.LanguageViewModel
import com.example.tfgonitime.viewmodel.SettingsViewModel // Importa SettingsViewModel para la foto de perfil
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

import java.util.Locale

@Composable
fun EditProfileScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val locale by languageViewModel.locale
    val selectedProfilePictureResource by settingsViewModel.profilePictureResource.collectAsState()

    val languages = listOf(
        "Español (España)" to Locale("es"),
        "Inglés (Reino Unido)" to Locale("en"),
        "Gallego" to Locale("gl")
    )

    var selectedLanguage by remember {
        mutableStateOf(
            languages.find { it.second.language == locale.language }?.first ?: languages[0].first
        )
    }

    var firstName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    val profilePictures = listOf(
        R.drawable.head_onigiri,
        R.drawable.head_daifuku,
        R.drawable.head_taiyaki,
        R.drawable.head_takoyaki,
        R.drawable.head_coffe_jelly
    )

    LaunchedEffect(authViewModel.userName) {
        authViewModel.userName.collect { name ->
            firstName = name.orEmpty()
        }
    }

    LaunchedEffect(locale) {
        selectedLanguage = languages.find { it.second.language == locale.language }?.first
            ?: languages[0].first
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp)
        ) {
            HeaderArrow(
                onClick = { navHostController.popBackStack() },
                title = stringResource(R.string.settings_edit_profile)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(Green.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            )
            {
                // Usar la imagen de perfil del SettingsViewModel
                Image(
                    painter = painterResource(id = selectedProfilePictureResource),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(85.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))


            val selectAvatarText = stringResource(R.string.settings_select_avatar)

            Text(
                text = selectAvatarText,
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
                    .padding(bottom = 15.dp, top = 4.dp),
                thickness = 2.dp,
                color = Green // Consider usar un color del tema o onPrimary
            )
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
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                if (resourceId == selectedProfilePictureResource)
                                    Brown.copy(alpha = 0.8f)
                                else
                                    Color.Transparent
                            )
                            .border(
                                width = 2.dp,
                                color = Brown.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                            .clickable {
                                settingsViewModel.setProfilePicture(resourceId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = resourceId),
                            contentDescription = "Avatar Option",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(50.dp)
                        )
                    }


                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val nameText = stringResource(R.string.name_hint)
            Text(
                text = nameText,
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
                    .padding(bottom = 25.dp, top = 4.dp),
                thickness = 2.dp,
                color = Green // Consider usar un color del tema o onPrimary
            )

            CustomTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = stringResource(R.string.name_hint),
                placeholder = stringResource(R.string.name_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
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
                    authViewModel.updateUserNameInProfile(
                        firstName,
                        context,
                        onSuccess = {
                            navHostController.popBackStack()
                        },
                        onError = { error ->
                            errorMessage = error
                            isErrorVisible = true
                        }
                    )
                },
                buttonText = stringResource(R.string.save),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                backgroundColor = Green,
                textColor = White
            )
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
