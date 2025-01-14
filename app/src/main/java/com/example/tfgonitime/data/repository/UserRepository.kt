package com.example.tfgonitime.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users") // Colección "users"

    /**
     * Obtiene los correos de todos los usuarios en la colección "users".
     * @return Lista de correos electrónicos.
     */
    suspend fun getAllUserEmails(): List<String> {
        return try {
            val snapshot = usersCollection.get().await() // Obtiene los documentos de la colección
            snapshot.documents.mapNotNull { document ->
                document.getString("email") // Extrae el campo "email" de cada documento
            }
        } catch (e: Exception) {
            // Manejar el error según sea necesario
            throw Exception("Error al obtener los correos de los usuarios: ${e.message}")
        }
    }

    /**
     * Verifica si un correo electrónico está registrado en Firestore.
     * @param email Correo a verificar.
     * @return `true` si el correo está registrado, `false` en caso contrario.
     */
    suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .get()
                .await() // Consulta Firestore
            !snapshot.isEmpty // Devuelve true si hay documentos
        } catch (e: Exception) {
            throw Exception("Error al verificar el correo: ${e.message}")
        }
    }
}