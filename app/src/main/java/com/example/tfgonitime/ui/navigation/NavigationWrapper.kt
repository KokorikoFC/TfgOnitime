package com.example.tfgonitime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tfgonitime.ui.screens.home.HomeScreen
import com.example.tfgonitime.ui.screens.login.LoginScreen
import com.example.tfgonitime.ui.screens.login.ChangePasswordScreen
import com.example.tfgonitime.ui.screens.setting.SettingScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpAgeScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpEmailScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpGenderScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpNameScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpPasswordScreen
import com.example.tfgonitime.ui.screens.splashScreen.LoadingScreen
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.ui.screens.splashScreen.SplashScreen
import com.example.tfgonitime.viewmodel.LanguageViewModel


@Composable
fun NavigationWrapper(navHostController: NavHostController, authViewModel: AuthViewModel, languageViewModel: LanguageViewModel) {
    NavHost(navController = navHostController, startDestination = "splashScreen") {

        /*----------------------------PANTALLA INICIAL (SPLASH)----------------------*/
        composable("splashScreen") { SplashScreen(navHostController, authViewModel, languageViewModel) }
        composable("loadingScreen") { LoadingScreen(navHostController, authViewModel) }

        /*----------------------------PANTALLAS DE LOGIN---------------------*/
        composable("loginScreen") { LoginScreen(navHostController, authViewModel) }
        composable("changePasswordScreen") { ChangePasswordScreen(navHostController, authViewModel) }

        /*----------------------------PANTALLAS DE REGISTRO---------------------*/
        composable("signUpNameScreen") { SignUpNameScreen(navHostController, authViewModel) }
        composable("signUpEmailScreen") { SignUpEmailScreen(navHostController, authViewModel) }
        composable("signUpGenderScreen") { SignUpGenderScreen(navHostController, authViewModel) }
        composable("signUpAgeScreen") { SignUpAgeScreen(navHostController, authViewModel) }
        composable("signUpPasswordScreen") { SignUpPasswordScreen(navHostController, authViewModel) }

        /*----------------------------PANTALLA PRINCIPAL (HOME)----------------------*/
        composable("homeScreen") { HomeScreen(navHostController) }

        /*----------------------------PANTALLAS DE AJUSTES---------------------*/
        composable("settingScreen") { SettingScreen(navHostController, authViewModel, languageViewModel) }
    }
}













