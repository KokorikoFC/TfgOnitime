package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users") // Colección "users"
    private val streaksCollection = firestore.collection("streaks")
    private val moodsCollection = firestore.collection("moods")

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

    // Metod para crear el documento de racha del usuario en Firestore
    suspend fun createStreakDocument(userId: String): Result<Boolean> {
        val streak = Streak.Streak(
            streakCount = 0,
            checkInCount = 0,
            lastLoginDate = Timestamp.now()
        )

        return try {
            firestore.collection("streaks")
                .document(userId)
                .set(streak)
                .await() // Guardar el documento de streak en Firestore
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

    suspend fun getUserDetails(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Error al obtener los detalles del usuario: ${e.message}")
        }
    }

    suspend fun deleteUserData(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val batch = firestore.batch()

            // Delete user document
            val userRef = usersCollection.document(userId)
            batch.delete(userRef)

            // Delete streak document
            val streakRef = streaksCollection.document(userId)
            batch.delete(streakRef)

            // Delete mood document
            val moodRef = moodsCollection.document(userId)
            batch.delete(moodRef)

            // Delete all tasks associated with the user (assuming tasks are in a subcollection named "tasks" under the user document)
            val tasksRef = usersCollection.document(userId).collection("tasks")
            val tasksSnapshot = tasksRef.get().await()
            for (document in tasksSnapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user data for $userId: ${e.message}")
            Result.failure(e)
        }
    }}