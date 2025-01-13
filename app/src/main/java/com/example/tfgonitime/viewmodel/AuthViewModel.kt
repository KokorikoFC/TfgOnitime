package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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


    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Verificar si los campos están vacíos
        if (email.isBlank() || password.isBlank()) {
            onError("Por favor ingrese correo y contraseña")
            return
        }

        viewModelScope.launch {
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
                            "Error al iniciar sesión: credenciales incorrectas"
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            "Error al iniciar sesión: credenciales incorrectas"
                        }

                        else -> {
                            "Error al iniciar sesión: ${e.message}"
                        }
                    }
                    onError(errorMessage)
                }
        }
    }

    fun changePassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    // El correo de restablecimiento se envió correctamente
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    // Falló al enviar el correo
                    onError(e.message ?: "Error al enviar correo de restablecimiento")
                }
        }
    }




    fun setUserName(name: String) {
        _userName.value = name
    }
    fun setUserGender(gender: String) {
        _gender.value = gender
    }
    fun setBirthDate(day: Int, month: Int, year: Int) {
        _birthDate.value = LocalDate.of(year, month, day)
    }
    fun setUserEmail(email: String) {
        _userEmail.value = email
    }
    fun setPassword(password: String) {
        _userPassword.value = password
    }


    fun signupUser(onComplete: (Boolean, String?) -> Unit) {
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

        val userDocument = mapOf(
            "name" to _userName.value,
            "email" to _userEmail.value,
            "gender" to _gender.value,
            "birthDate" to birthDateMap, // Verificamos que birthDateMap no sea nulo
            "actualLevel" to 0,
            "coins" to 0,
            "taskCompleted" to 0,
            "darkModePreference" to false,
            "createdAt" to System.currentTimeMillis()
        )

        Log.d("Signup", "Guardando el documento de usuario en Firestore con ID: $userId")

        firestore.collection("users")
            .document(userId)
            .set(userDocument)
            .addOnSuccessListener {
                Log.d("Signup", "Documento creado exitosamente en Firestore.")
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                Log.d("Signup", "Error al crear documento en Firestore: ${e.message}")
                onComplete(false, e.message)
            }
    }




    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        _isAuthenticated.value = false
        onSuccess()
    }
}