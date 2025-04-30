package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Furniture // Asegúrate de que Furniture esté importada
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldPath
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FurnitureRepository {

    private val firestore = FirebaseFirestore.getInstance()
    // Asegúrate de que la colección principal sea 'furniture' si tus subcolecciones están dentro de 'furniture/categories'
    private val furnitureCollection = firestore.collection("furnitures")
    private val categoriesDocRef = furnitureCollection.document("categories")
    // Necesitamos acceso a la colección de usuarios para obtener el inventario
    private val usersCollection = firestore.collection("users")

    // Nombres de las subcolecciones donde están los muebles de la tienda
    private val subcollections = listOf(
        "floor_l_slot",
        "floor_r_slot",
        "left_l_wall",
        "left_r_wall",
        "right_wall",
        "rug"
    )

    // =========================================================================
    // Funciones para la Tienda
    // =========================================================================

    suspend fun getAllFurniture(): Result<List<Furniture>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val allFurniture = getAllFurnitureFromSubcollections() // Obtener todos los muebles
            Result.success(allFurniture)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getAllFurnitureGroupedByTheme(): Result<Map<String, List<Furniture>>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val furnitureList = getAllFurnitureFromSubcollections()
            val grouped = furnitureList.groupBy { it.theme.ifEmpty { "Sin tema" } }
            Result.success(grouped)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    private suspend fun getAllFurnitureFromSubcollections(): List<Furniture> = withContext(Dispatchers.IO) {
        return@withContext try {
            val allFurniture = coroutineScope {
                subcollections.map { slotName ->
                    async {
                        try {
                            val snapshot = categoriesDocRef.collection(slotName).get().await()
                            val furnitureFromSlot = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(Furniture::class.java)?.copy(
                                    id = doc.id,
                                    slot = slotName
                                )
                            }
                            furnitureFromSlot
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                }.awaitAll().flatten()
            }

            allFurniture
        } catch (e: Exception) {
            emptyList()
        }
    }

    // =========================================================================
    // Funciones para el Inventario del Usuario
    // =========================================================================


    suspend fun getUserInventoryFurnitureIds(userId: String): Result<List<String>> = withContext(Dispatchers.IO) {
        Log.d("FurnitureRepo", "Obteniendo IDs de inventario para usuario: $userId desde inventory/available")
        return@withContext try {
            val userInventoryDocRef = usersCollection.document(userId)
                .collection("inventory")
                .document("available")

            val documentSnapshot = userInventoryDocRef.get().await()

            if (documentSnapshot.exists()) {
                @Suppress("UNCHECKED_CAST")
                val furnitureIds = documentSnapshot.get("furniture") as? List<String>
                Result.success(furnitureIds ?: emptyList())
            } else {
                Result.success(emptyList()) // Inventario vacío si el documento no existe
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    private suspend fun getFurnitureDetailsByIdsFromSubcollections(furnitureIds: List<String>): Result<List<Furniture>> = withContext(Dispatchers.IO) {
        if (furnitureIds.isEmpty()) {
            return@withContext Result.success(emptyList())
        }

        Log.d("FurnitureRepo", "Obteniendo detalles de muebles para IDs: $furnitureIds desde subcolecciones")
        return@withContext try {
            // Reutilizamos la lógica que obtiene todos los muebles de subcolecciones
            val allFurnitureFromSubcollections = getAllFurnitureFromSubcollections()

            // Filtramos por las IDs del inventario del usuario
            val userInventoryDetails = allFurnitureFromSubcollections.filter { furniture ->
                furnitureIds.contains(furniture.id)
            }

            Result.success(userInventoryDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun getUserInventoryFurniture(userId: String): Result<List<Furniture>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. Obtener los IDs de los muebles del inventario del usuario
            val furnitureIdsResult = getUserInventoryFurnitureIds(userId)
            val furnitureIds = furnitureIdsResult.getOrNull()

            if (furnitureIdsResult.isFailure || furnitureIds.isNullOrEmpty()) {
                if (furnitureIdsResult.isFailure) {
                    return@withContext Result.failure(furnitureIdsResult.exceptionOrNull() ?: Exception("Error desconocido al obtener IDs de inventario COMPLETO."))
                } else {
                    return@withContext Result.success(emptyList())
                }
            }

            // 2. Obtener los detalles completos de esos muebles usando los IDs desde las subcolecciones
            val furnitureDetailsResult = getFurnitureDetailsByIdsFromSubcollections(furnitureIds)

            if (furnitureDetailsResult.isFailure) {
                return@withContext Result.failure(furnitureDetailsResult.exceptionOrNull() ?: Exception("Error desconocido al obtener detalles de inventario COMPLETO."))
            }

            Result.success(furnitureDetailsResult.getOrNull() ?: emptyList())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
