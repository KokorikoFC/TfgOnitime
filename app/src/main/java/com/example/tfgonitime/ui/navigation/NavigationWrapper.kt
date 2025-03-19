package com.example.tfgonitime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tfgonitime.ui.screen.StreakScreen
import com.example.tfgonitime.ui.screens.diary.DiaryScreen
import com.example.tfgonitime.ui.screens.diary.MoodEditScreen
import com.example.tfgonitime.ui.screens.diary.MoodScreen
import com.example.tfgonitime.ui.screens.diary.MoodSelectionScreen
import com.example.tfgonitime.ui.screens.home.HomeScreen
import com.example.tfgonitime.ui.screens.login.LoginScreen
import com.example.tfgonitime.ui.screens.login.ChangePasswordScreen
import com.example.tfgonitime.ui.screens.setting.EditProfileScreen
import com.example.tfgonitime.ui.screens.setting.SettingScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpAgeScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpEmailScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpGenderScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpNameScreen
import com.example.tfgonitime.ui.screens.signUp.SignUpPasswordScreen
import com.example.tfgonitime.ui.screens.splashScreen.LoadingScreen
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.ui.screens.splashScreen.SplashScreen
import com.example.tfgonitime.ui.screens.task.AddTaskGroupScreen
import com.example.tfgonitime.ui.screens.task.AddTaskScreen
import com.example.tfgonitime.ui.screens.task.DeleteGroupScreen
import com.example.tfgonitime.ui.screens.task.EditTaskScreen
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import com.example.tfgonitime.viewmodel.StreakViewModel
import java.time.LocalDate
import com.example.tfgonitime.viewmodel.TaskViewModel
import com.example.tfgonitime.ui.screens.chat.ChatScreen
import com.example.tfgonitime.ui.screens.missionScreen.MissionScreen
import com.example.tfgonitime.viewmodel.ChatViewModel
import com.example.tfgonitime.viewmodel.MissionViewModel


@Composable

fun NavigationWrapper(navHostController: NavHostController, authViewModel: AuthViewModel,taskViewModel:TaskViewModel, languageViewModel: LanguageViewModel, diaryViewModel: DiaryViewModel, groupViewModel: GroupViewModel, streakViewModel: StreakViewModel, chatViewModel: ChatViewModel, missionViewModel: MissionViewModel) {

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

        /*----------------------------PANTALLAS DE RACHAS---------------------*/
        composable("streakScreen") { StreakScreen(navHostController,streakViewModel) }


        /*----------------------------PANTALLA PRINCIPAL (HOME)----------------------*/
        composable("homeScreen") { HomeScreen(navHostController,taskViewModel, groupViewModel) }

        /*----------------------------PANTALLAS DE TAREAS---------------------*/
        composable("addTaskScreen") { AddTaskScreen(navHostController,  taskViewModel = taskViewModel, groupViewModel = groupViewModel) }
        composable(route = "editTaskScreen/{taskId}", arguments = listOf(navArgument("taskId") { type = NavType.StringType })) {
            backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            taskId?.let {
                // Recuperar el Task por taskId desde el ViewModel
                val task = taskViewModel.getTaskById(taskId)

                if (task != null) {
                    EditTaskScreen(navHostController = navHostController, taskViewModel = taskViewModel, groupViewModel = groupViewModel, taskToEdit = task)
                }
            }
        }
        composable("addTaskGroupScreen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            AddTaskGroupScreen(navHostController, groupViewModel, userId)
        }
        composable("deleteGroupScreen") { DeleteGroupScreen(navHostController, groupViewModel) }


        /*----------------------------PANTALLAS DE AJUSTES---------------------*/
        composable("settingScreen") { SettingScreen(navHostController, authViewModel, languageViewModel) }
        composable ("editProfileScreen") { EditProfileScreen(navHostController, authViewModel, languageViewModel) }

        /*----------------------------PANTALLAS DE DIARIO---------------------*/
        composable("diaryScreen") { DiaryScreen(navHostController,diaryViewModel) }
        composable(
            route = "moodSelectionScreen/{selectedDate}",
            arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDateString = backStackEntry.arguments?.getString("selectedDate")
            val selectedDate = LocalDate.parse(selectedDateString) // Convierte el string a LocalDate
            MoodSelectionScreen(navHostController, selectedDate = selectedDate, diaryViewModel)
        }
        composable("moodEditScreen/{moodDate}") { backStackEntry ->
            val moodDate = backStackEntry.arguments?.getString("moodDate") ?: ""
            MoodEditScreen(navHostController, diaryViewModel, moodDate)
        }
        composable("moodScreen/{moodDate}") {backStackEntry ->
            val moodDate = backStackEntry.arguments?.getString("moodDate") ?: ""
            MoodScreen(navHostController, diaryViewModel, moodDate)
        }

        /*----------------------------PANTALLAS DE CHAT---------------------*/
        composable("chatScreen") { ChatScreen(navHostController, chatViewModel) }

        /*---------------------------PANTALLA MISIONES----------------------------*/
        composable("missionScreen") { MissionScreen(navHostController, missionViewModel) }

    }
}






















