package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldPath

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users") // Colección "users"
    private val streaksCollection = firestore.collection("streaks")
    private val moodsCollection = firestore.collection("moods") // Asegúrate de tener esta colección definida si la usas en deleteUserData

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

    // Funcion para obtner el nombre del usario logueado
    suspend fun getUserName(userId: String): Result<String> {
        return try {
            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val user = documentSnapshot.toObject(User::class.java)
            val userName = user?.userName ?: ""  // Retorna el nombre del usuario o una cadena vacía si no existe

            Result.success(userName)
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
    }
    // Función para obtener las monedas del usuario
    suspend fun getUserCoins(userId: String): Result<Int> {
        return try {
            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val user = documentSnapshot.toObject(User::class.java)
            val coins = user?.coins ?: 0  // Retorna las monedas o 0 si no existe

            Result.success(coins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getCurrentPetId(userId: String): Result<String?> {
        return try {
            val documentSnapshot = usersCollection
                .document(userId)
                .get()
                .await()
            val currentPetId = documentSnapshot.getString("userPetId") // Asume que el campo se llama "currentPetId"
            Result.success(currentPetId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCurrentPetId(userId: String, petId: String): Result<Unit> {
        return try {
            val userRef = usersCollection.document(userId)
            userRef.update("userPetId", petId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating userPetId for user $userId: ${e.message}", e)
            Result.failure(e)
        }
    }



}