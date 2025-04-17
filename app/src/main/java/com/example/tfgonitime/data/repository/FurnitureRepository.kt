package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Furniture
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FurnitureRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val categoriesDocRef = firestore.collection("furnitures").document("categories")

    private val subcollections = listOf(
        "floor_l_slot",
        "floor_r_slot",
        "left_l_wall",
        "left_r_wall",
        "right_wall",
        "rug"
    )

    /**
     * Obtiene todos los muebles de todas las subcolecciones dentro de "furniture/categories",
     * y los agrupa por tema.
     *
     * @return Un Result con un mapa de <Theme, List<Furniture>>.
     */
    suspend fun getAllFurnitureGroupedByTheme(): Result<Map<String, List<Furniture>>> {
        return try {
            val furnitureList = getAllFurniture()  // Obtener todos los muebles
            val grouped = furnitureList.groupBy { it.theme.ifEmpty { "Sin tema" } }  // Agrupar por tema
            Result.success(grouped)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lee todos los muebles de todas las subcolecciones y los convierte en una lista de objetos Furniture.
     */
    private suspend fun getAllFurniture(): List<Furniture> {
        return try {
            val allFurniture = subcollections.map { slotName ->
                val snapshot = categoriesDocRef.collection(slotName).get().await()  // Obtener documentos de la subcolección
                val furnitureFromSlot = snapshot.documents.mapNotNull { doc ->
                    // Crear objeto Furniture a partir del documento, asignando el id y slot correspondiente
                    doc.toObject(Furniture::class.java)?.copy(
                        id = doc.id,  // Asignar el id del documento
                        slot = slotName  // Asignar el slot (subcolección) del mueble
                    )
                }
                furnitureFromSlot
            }.flatten()  // Obtener todos los muebles de todas las subcolecciones

            allFurniture
        } catch (e: Exception) {
            emptyList()
        }
    }
}
