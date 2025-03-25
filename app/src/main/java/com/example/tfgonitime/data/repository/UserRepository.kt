package com.example.tfgonitime.data.repository

import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue


class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users") // Colección "users"
    private val streaksCollection = firestore.collection("streaks")

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

    suspend fun createUserDocument(userId: String, user: User): Result<Boolean> {
        return try {
            firestore.collection("users")
                .document(userId)
                .set(user)
                .await() // Guardar el documento en Firestore
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun createMoodDocument(userId: String): Result<Boolean> {
        val mood = Mood(
            id = userId,
            moodDate = "22/01/2025", // Fecha predeterminada
            moodType = "Happy", // Estado de ánimo predeterminado
            diaryEntry = "Today was a great day!" // Entrada predeterminada
        )

        return try {
            firestore.collection("moods")
                .document(userId)
                .set(mood)
                .await() // Guardar el documento de mood en Firestore
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para obtener el número de tareas completadas del usuario
    suspend fun getUserTasksCompleted(userId: String): Result<Int> {
        return try {
            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val user = documentSnapshot.toObject(User::class.java)
            val tasksCompleted = user?.tasksCompleted ?: 0  // Retorna las tareas completadas o 0 si no existe

            Result.success(tasksCompleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para incrementar el contador de tareas completadas del usuario
    suspend fun incrementTasksCompleted(userId: String): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            userRef.update("tasksCompleted", FieldValue.increment(1)).await()  // Incrementa el valor de "tasksCompleted"
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Función para añadir monedas
    suspend fun addCoins(userId: String, coins: Int): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            userRef.update("coins", FieldValue.increment(coins.toLong())).await()  // Incrementa el valor de "coins"
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}