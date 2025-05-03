package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Pets // Asegúrate de que la clase Pets esté definida
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PetsRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val petsCollection = firestore.collection("pets")

    suspend fun getAllPets(): List<Pets> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = petsCollection.get().await()
            val petsList = snapshot.documents.mapNotNull { document ->
                document.toObject(Pets::class.java)?.copy(id = document.id)
            }
            petsList.forEach { pet ->
                Log.d("Firestore", "Mascota obtenida (Repository): ID=${pet.id}, pose1=${pet.pose1}, pose2=${pet.pose2}")
            }
            petsList
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener las mascotas (Repository): ${e.message}")
            emptyList()
        }
    }
}