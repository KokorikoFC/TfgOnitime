package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.model.TaskGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class GroupRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función para agregar un grupo
    suspend fun addGroup(userId: String, group: TaskGroup): Result<String> {
        return try {
            val userGroupsRef = db.collection("users")
                .document(userId)
                .collection("taskGroups")

            val newGroupRef = userGroupsRef.document() //  Generamos el ID primero

            val groupWithId = group.copy(groupId = newGroupRef.id) // Asignamos el ID al objeto

            newGroupRef.set(groupWithId).await() // Guardamos el objeto con el ID correcto

            Result.success(newGroupRef.id) //  Devolvemos el ID generado
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Función para obtener todos los grupos de un usuario
    suspend fun getGroups(userId: String): Result<List<TaskGroup>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("taskGroups")
                .get()
                .await()

            val groups = snapshot.documents.mapNotNull { it.toObject(TaskGroup::class.java) }
            Result.success(groups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función suspendida para actualizar una tarea
    suspend fun updateGroup(userId: String, groupId: String, updatedGroup: TaskGroup): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("taskGroups")
                .document(groupId)
                .set(updatedGroup)
                .await()  // Espera hasta que se complete la operación de actualización

            // Retorna un resultado exitoso
            Result.success(Unit)
        } catch (e: Exception) {
            // Si hubo un error, retorna el error
            Result.failure(e)
        }
    }
    // Función para eliminar un grupo
    suspend fun deleteGroup(userId: String, groupId: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("taskGroups")
                .document(groupId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupIdByName(userId: String, groupName: String): Result<String> {
        return try {
            // Consulta para obtener el documento del grupo por su nombre
            val querySnapshot = db.collection("users")
                .document(userId)
                .collection("taskGroups")
                .whereEqualTo("groupName", groupName)  // Filtramos por nombre de grupo
                .get()
                .await()

            // Verificamos si encontramos el grupo
            if (querySnapshot.isEmpty) {
                Result.failure(Exception("Grupo no encontrado"))
            } else {
                // Asumimos que solo hay un grupo con el nombre dado, obtenemos el ID
                val groupId = querySnapshot.documents.first().id
                Result.success(groupId)  // Retornamos el ID del grupo
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNameById(userId: String, groupId: String): Result<String> {
        return try {
            // Consulta para obtener el documento del grupo por su ID
            val docSnapshot = db.collection("users")
                .document(userId)
                .collection("taskGroups")
                .document(groupId)
                .get()
                .await()

            // Verificamos si encontramos el grupo
            if (docSnapshot.exists()) {
                val groupName = docSnapshot.toObject(TaskGroup::class.java)?.groupName
                if (groupName != null) {
                    Result.success(groupName)
                } else {
                    Result.failure(Exception("Nombre de grupo no encontrado"))
                }
            } else {
                Result.failure(Exception("Grupo no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}

