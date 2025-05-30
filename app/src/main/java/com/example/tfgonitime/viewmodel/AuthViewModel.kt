package com.example.tfgonitime.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.User
import com.example.tfgonitime.data.repository.FurnitureRepository
import com.example.tfgonitime.data.repository.MissionRepository
import com.example.tfgonitime.data.repository.StreakRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _gender = MutableStateFlow<String?>(null)
    val gender: StateFlow<String?> = _gender

    private val _birthDate = MutableStateFlow<LocalDate?>(null)
    val birthDate: StateFlow<LocalDate?> = _birthDate

    private val _userPassword = MutableStateFlow<String?>(null)
    val userPassword: StateFlow<String?> = _userPassword

    private val userRepository = UserRepository()
    private val missionRepository = MissionRepository()

    private val diaryViewModel = DiaryViewModel()
    private val furnitureRepository = FurnitureRepository()

    private val streakRepository = StreakRepository()

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        _isAuthenticated.value = auth.currentUser != null
        auth.currentUser?.let { user ->
            _userEmail.value = user.email
            _userId.value = user.uid
            fetchUserDetails(user.uid)
        }
    }

    private fun fetchUserDetails(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserDetails(userId)
                _userName.value = user?.userName
                // Puedes obtener otros detalles del usuario del objeto 'user' si es necesario
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error fetching user details: ${e.message}")
            }
        }
    }


    fun login(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        // Verificar si los campos están vacíos
        if (email.isBlank() || password.isBlank()) {
            onError(context.getString(R.string.login_empty_fields))
            return
        }

        viewModelScope.launch {

            // Primero, valida si el correo está registrado
            val isRegistered = userRepository.isEmailRegistered(email)

            if (!isRegistered) {
                onError(context.getString(R.string.email_not_registered)) // Muestra el error si no está registrado
                return@launch
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    _isAuthenticated.value = true
                    _userEmail.value = auth.currentUser?.email
                    authResult.user?.uid?.let { fetchUserDetails(it) }
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    // Extraemos el código de error para mensajes específicos
                    val errorMessage = when (e) {
                        is FirebaseAuthInvalidUserException -> {
                            context.getString(R.string.login_error)
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            context.getString(R.string.login_error_password)
                        }

                        else -> {
                            "Error al iniciar sesión: ${e.message}"
                        }
                    }
                    onError(errorMessage)
                }
        }
    }



    fun changePassword(
        email: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Verificar si el campo está vacío
        if (email.isBlank()) {
            onError(context.getString(R.string.forgot_password_empty_field))
            return
        }

        viewModelScope.launch {
            try {
                // Validar si el correo está registrado usando la nueva función
                val isRegistered = userRepository.isEmailRegistered(email)

                if (!isRegistered) {
                    onError(context.getString(R.string.email_not_registered))
                    return@launch
                }

                // Si el correo está registrado, enviar el correo de restablecimiento
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        onSuccess() // El correo de restablecimiento se envió correctamente
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: context.getString(R.string.reset_password_error))
                    }

            } catch (e: Exception) {
                // Manejar errores al acceder al repositorio
                onError("Error al validar los usuarios")
            }
        }
    }


    fun setUserName(
        userName: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userName.isBlank()) {
            onError(context.getString(R.string.signup_error_name_empty))
        } else {
            _userName.value = userName
            onSuccess()
        }
    }

    fun updateUserNameInProfile(
        userName: String,
        context: Context, // Mantén context para strings de error si es necesario
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userName.isBlank()) {
            onError(context.getString(R.string.signup_error_name_empty)) // Puedes usar el mismo string o uno nuevo
            return
        }

        // Asegurarse de tener el userId antes de intentar guardar en Firebase
        val currentUserId = _userId.value
        if (currentUserId == null) {
            onError("User not logged in.") // Mensaje de error si el usuario no está logueado
            Log.e("AuthViewModel", "Attempted to update name but user ID is null.")
            return
        }

        // Lanzar una corrutina para realizar la operación asíncrona de guardado en Firebase
        viewModelScope.launch {
            val result = userRepository.updateUserName(currentUserId, userName) // <-- Llamada al Repository para guardar en Firebase

            if (result.isSuccess) {
                // Si se guardó correctamente en Firebase, actualiza el StateFlow local
                _userName.value = userName
                onSuccess() // Llama al callback de éxito (ej. mostrar un mensaje)
                Log.d("AuthViewModel", "User name updated successfully in Firestore and ViewModel")
            } else {
                // Si hubo un error al guardar en Firebase, llama al callback de error
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error saving user name"
                onError("Error al guardar nombre: $errorMessage") // Pasa el error a la UI
                Log.e("AuthViewModel", "Failed to update user name in Firestore: $errorMessage")
            }
        }
    }


    fun setUserGender(
        gender: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        if (gender.isBlank()) {
            onError(context.getString(R.string.register_error_gender_empty))
        } else {
            _gender.value = gender
            onSuccess()
        }
    }

    fun setBirthDate(
        day: Int,
        month: Int,
        year: Int,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Verificar que la fecha no esté vacía
        if (day == 0 || month == 0 || year == 0) {
            onError(context.getString(R.string.register_error_birth_date))
            return
        }

        // Verificar que el usuario tenga al menos 5 años
        val birthDate = LocalDate.of(year, month, day)
        val currentDate = LocalDate.now()
        val age = currentDate.year - birthDate.year
        if (age < 5) {
            onError(context.getString(R.string.register_error_birth_date))
            return
        }

        // Si pasa las validaciones, asignamos la fecha y ejecutamos el éxito
        _birthDate.value = birthDate
        onSuccess()
    }

    fun setUserEmail(
        email: String,
        repeatEmail: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Verificar si el campo de correo está vacío
        if (email.isBlank()) {
            onError(context.getString(R.string.register_error_field_empty))
            return
        }

        // Validar que el correo esté en el formato correcto
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            onError(context.getString(R.string.register_error_invalid_email))
            return
        }

        if (repeatEmail != email) {
            onError(context.getString(R.string.register_error_email_mismatch))
            return
        }

        viewModelScope.launch {
            try {
                val isRegistered = userRepository.isEmailRegistered(email)

                if (isRegistered) {
                    onError(context.getString(R.string.register_already_exist_email))
                    return@launch
                }

                _userEmail.value = email
                onSuccess()
            } catch (e: Exception) {
                onError(context.getString(R.string.register_error_invalid_email))
            }
        }
    }



    fun setPassword(
        password: String,
        repeatPassword: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Validar que la contraseña no esté vacía
        if (password.isBlank()) {
            onError(context.getString(R.string.register_error_field_empty))  // Puedes personalizar este mensaje en tu archivo strings.xml
            return
        }
        // Validar que las contraseñas coincidan
        if (password != repeatPassword) {
            onError(context.getString(R.string.register_error_password_mismatch))  // Mensaje para contraseñas no coincidentes
            return
        }
        // Validar la longitud mínima de la contraseña (por ejemplo, al menos 6 caracteres)
        if (password.length < 6) {
            onError(context.getString(R.string.register_error_password_too_short))  // Puedes personalizar este mensaje
            return
        }

        // Si pasa todas las validaciones, asignamos la contraseña y ejecutamos el éxito
        _userPassword.value = password
        onSuccess()
    }


    //---------------Registro de usuario----------------
    suspend fun signupUser(onComplete: (Boolean, String?) -> Unit) {
        val email = _userEmail.value
        val password = _userPassword.value

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Log.d("Signup", "Email o contraseña vacíos")
            onComplete(false, "Email o contraseña no pueden estar vacíos")
            return
        }

        try {
            // Crear el usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                Log.d("Signup", "Usuario creado exitosamente, UID: $userId")

                // Crear el documento de usuario
                val birthDateMap = _birthDate.value?.let {
                    mapOf(
                        "day" to it.dayOfMonth,
                        "month" to it.monthValue,
                        "year" to it.year
                    )
                }

                val user = User(
                    userName = _userName.value.orEmpty(),
                    birthDate = birthDateMap,
                    gender = _gender.value.orEmpty(),
                    email = email,
                    actualLevel = 0,
                    coins = 0,
                    tasksCompleted = 0,
                    createdAt = System.currentTimeMillis(),
                    coinsYear = 0,
                    diaryEntryYear = 0,
                    messagesOniYear = 0,
                    yearRef = currentYear,
                    totalActiveDays = 1,
                    lastActiveDate = today
                )

                val streak = Streak(
                    currentStreak = 0,
                    longestStreak = 0,
                    lastCheckIn = Timestamp.now()
                )

                // Crear el documento del usuario en Firestore
                val createUserResult = userRepository.createUserDocument(userId, user)
                if (createUserResult.isFailure) {
                    onComplete(false, createUserResult.exceptionOrNull()?.message ?: "Error al crear usuario")
                    return
                }

                // Inicializar inventario con documento "available"
                val initInventoryResult = furnitureRepository.initializeUserInventory(userId)
                if (initInventoryResult.isFailure) {
                    onComplete(false, initInventoryResult.exceptionOrNull()?.message ?: "Error al inicializar inventario")
                    return
                }

                // Inicializar streak
                val initStreak = streakRepository.initializeStreak(userId, streak)
                if (initStreak.isFailure) {
                    onComplete(false, initStreak.exceptionOrNull()?.message ?: "Error al inicializar streak")
                    return
                }

                // Asignar misiones iniciales
                val assignMissionsResult = missionRepository.assignInitialMissions(userId)
                if (assignMissionsResult.isFailure) {
                    onComplete(false, assignMissionsResult.exceptionOrNull()?.message ?: "Error al asignar misiones")
                    return
                }

                onComplete(true, null)

            } else {
                Log.d("Signup", "No se pudo obtener el UID del usuario.")
                onComplete(false, "No se pudo obtener el UID del usuario.")
            }

        } catch (e: Exception) {
            Log.d("Signup", "Error en la creación del usuario: ${e.message}")
            onComplete(false, e.message)
        }
    }


    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        _isAuthenticated.value = false
        _userId.value = null
        _userEmail.value = null
        _userName.value = null
        _gender.value = null
        _birthDate.value = null
        diaryViewModel.clearSelectedMood()
        onSuccess()
    }

    fun updatePassword(
        currentPassword: String,
        newPassword: String,
        context: Context, // Para acceder a strings de recursos
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser // Obtiene el usuario actualmente logueado

        if (user == null) {
            onError(context.getString(R.string.update_password_error_not_logged_in)) // Crea este string
            return
        }

        // Re-autenticar al usuario con su contraseña actual
        val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    Log.d("AuthViewModel", "User re-authenticated successfully.")

                    // Una vez re-autenticado, procede a actualizar la contraseña
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.d("AuthViewModel", "User password updated.")
                                onSuccess() // Llama al callback de éxito
                            } else {
                                Log.e("AuthViewModel", "Error updating password: ${updateTask.exception?.message}")
                                val errorMessage = when (updateTask.exception) {
                                    is FirebaseAuthRecentLoginRequiredException -> context.getString(R.string.update_password_recent_login_required) // Crea este string
                                    else -> updateTask.exception?.message ?: context.getString(R.string.update_password_generic_error) // Crea este string
                                }
                                onError(errorMessage) // Pasa el error a la UI
                            }
                        }
                } else {
                    Log.e("AuthViewModel", "Error re-authenticating user: ${reauthTask.exception?.message}")
                    val errorMessage = when (reauthTask.exception) {
                        is FirebaseAuthInvalidCredentialsException -> context.getString(R.string.update_password_error_wrong_current) // Crea este string
                        else -> reauthTask.exception?.message ?: context.getString(R.string.update_password_reauth_generic_error) // Crea este string
                    }
                    onError(errorMessage) // Pasa el error a la UI
                }
            }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        val user = auth.currentUser
        user?.let { currentUser ->
            viewModelScope.launch {
                try {
                    // Delete user data from Firestore
                    val deleteFirestoreResult = userRepository.deleteUserData(currentUser.uid)
                    if (deleteFirestoreResult.isFailure) {
                        Log.e("AuthViewModel", "Error deleting Firestore data: ${deleteFirestoreResult.exceptionOrNull()?.message}")
                    }

                    // Now delete the user's account from Firebase Authentication
                    currentUser.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("AuthViewModel", "User account deleted.")
                            } else {
                                Log.e("AuthViewModel", "Error deleting user account: ${task.exception?.message}")
                            }
                            _isAuthenticated.value = false
                            _userEmail.value = null
                            _userId.value = null
                            _userName.value = null
                            onComplete()
                        }
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error deleting user account: ${e.message}")

                    // Redirigir incluso si ocurre una excepción
                    _isAuthenticated.value = false
                    _userEmail.value = null
                    _userId.value = null
                    _userName.value = null
                    onComplete()
                }
            }
        }
    }
}