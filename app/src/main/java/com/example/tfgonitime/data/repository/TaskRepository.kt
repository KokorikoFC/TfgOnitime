package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class TaskRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función suspendida para agregar una tarea
    suspend fun addTask(userId: String, task: Task): Result<String> {
        return try {
            val documentReference = db.collection("users")
                .document(userId)
                .collection("tasks")
                .add(task)
                .await()

            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Función suspendida para obtener todas las tareas de un usuario
    suspend fun getTasks(userId: String): Result<List<Task>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .await()

            // Verifica el contenido de la consulta
            Log.d("TaskRepository", "Tareas obtenidas: ${snapshot.documents.size}")

            val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Función suspendida para actualizar una tarea
    suspend fun updateTask(userId: String, taskId: String, updatedTask: Task): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .set(updatedTask)
                .await()  // Espera hasta que se complete la operación de actualización

            // Retorna un resultado exitoso
            Result.success(Unit)
        } catch (e: Exception) {
            // Si hubo un error, retorna el error
            Result.failure(e)
        }
    }

    // Función suspendida para eliminar una tarea
    suspend fun deleteTask(userId: String, taskId: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()  // Espera hasta que se complete la operación de eliminación

            // Retorna un resultado exitoso
            Result.success(Unit)
        } catch (e: Exception) {
            // Si hubo un error, retorna el error
            Result.failure(e)
        }
    }
}
