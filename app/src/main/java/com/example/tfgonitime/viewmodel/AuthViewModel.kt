package com.example.tfgonitime.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.User
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        _isAuthenticated.value = auth.currentUser != null
        auth.currentUser?.let { user ->
            _userEmail.value =
                user.email  // Aquí actualizas el email cuando hay un usuario autenticado
            _userId.value = user.uid
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
                .addOnSuccessListener {
                    _isAuthenticated.value = true
                    _userEmail.value = auth.currentUser?.email // Actualizamos el email aquí también
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
        _userEmail.value = email
        onSuccess()
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

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    Log.d("Signup", "Usuario creado exitosamente, UID: $userId")
                    if (userId != null) {
                        createUserDocument(userId, onComplete)
                        createStreakDocument(userId)
                        createTaskDocument(userId)
                    } else {
                        Log.d("Signup", "No se pudo obtener el UID del usuario.")
                        onComplete(false, "No se pudo obtener el UID del usuario.")
                    }
                } else {
                    Log.d("Signup", "Error en la creación del usuario: ${task.exception?.message}")
                    onComplete(false, task.exception?.message)
                }
            }
    }

    // Crear el documento de usuario en Firestore
    private fun createUserDocument(userId: String, onComplete: (Boolean, String?) -> Unit) {
        val birthDateMap = _birthDate.value?.let {
            mapOf(
                "day" to it.dayOfMonth,
                "month" to it.monthValue,
                "year" to it.year
            )
        } ?: run {
            Log.d("Signup", "Fecha de nacimiento no proporcionada")
            null
        }

        val user = User(
            userName = _userName.value.orEmpty(),
            birthDate = birthDateMap,
            gender = _gender.value.orEmpty(),
            email = _userEmail.value.orEmpty(),
            actualLevel = 0,
            coins = 0,
            tasksCompleted = 0,
            createdAt = System.currentTimeMillis()
        )

        Log.d("Signup", "Guardando el documento de usuario en Firestore con ID: $userId")

        firestore.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Signup", "Documento creado exitosamente en Firestore.")
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                Log.d("Signup", "Error al crear documento en Firestore: ${e.message}")
                onComplete(false, e.message)
            }
    }


    private fun createStreakDocument(userId: String) {
        // Crear la instancia de Streak con la lista predeterminada de días
        val streak = Streak.Streak(
            streakCount = 0,
            checkInCount = 0,
            lastLoginDate = Timestamp.now()
        )

        // Guardar el documento en Firestore
        firestore.collection("streaks")
            .document(userId)  // El ID de documento será el userId
            .set(streak)
            .addOnSuccessListener {
                Log.d("Firestore", "Documento de streak creado exitosamente")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creando el documento", e)
            }
    }

    private fun createTaskDocument(userId: String){
        val task = Task(
            userId = userId,
            taskName = "Tarea de prueba",
        )

        firestore.collection("tasks")
            .document(userId)
            .set(task)
            .addOnSuccessListener {
                Log.d("Firestore", "Documento de tarea creado exitosamente")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creando el documento", e)
            }

    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        _isAuthenticated.value = false
        onSuccess()
    }
}