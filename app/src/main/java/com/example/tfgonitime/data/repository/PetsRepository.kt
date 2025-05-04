package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Pets // Asegúrate de que la clase Pets esté definida
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PetsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    val petsCollection = firestore.collection("pets")

    suspend fun getAllPets(): List<Pets> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = petsCollection.get().await()
            val petsList = snapshot.documents.mapNotNull { document ->
                document.toObject(Pets::class.java)?.copy(id = document.id)
            }
            petsList
        } catch (e: Exception) {
            Log.e("PetsRepository", "Error al obtener todas las mascotas: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPetById(petId: String): Pets? = withContext(Dispatchers.IO) {
        return@withContext try {
            val documentSnapshot = petsCollection.document(petId).get().await()
            if (documentSnapshot.exists()) {
                val pet = documentSnapshot.toObject(Pets::class.java)?.copy(id = documentSnapshot.id)
                pet
            } else {
                Log.d("PetsRepository", "Mascota con ID $petId no encontrada.")
                null
            }
        } catch (e: Exception) {
            Log.e("PetsRepository", "Error al obtener mascota por ID $petId: ${e.message}", e)
            null
        }
    }
}