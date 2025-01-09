package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        _isAuthenticated.value = auth.currentUser != null
        auth.currentUser?.let { user ->
            _userEmail.value = user.email  // Actualizar el email cuando hay un usuario autenticado
            _userId.value = user.uid
        }
    }
    fun signup(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    _isAuthenticated.value = true
                    _userEmail.value = authResult.user?.email // Esto asegura que el correo se asigne correctamente
                    onSuccess()
                }
                .addOnFailureListener { e -> onError(e.message ?: "Error al registrarse") }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _isAuthenticated.value = true
                    _userEmail.value = auth.currentUser?.email // Actualizamos el email aquí también
                    onSuccess()
                }
                .addOnFailureListener { e -> onError(e.message ?: "Error al iniciar sesión") }
        }
    }
}