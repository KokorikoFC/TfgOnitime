package com.example.tfgonitime.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tfgonitime.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await


class TaskRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función suspendida para agregar una tarea
    suspend fun addTask(userId: String, task: Task): Result<String> {
        return try {
            // Agregar tarea a la subcolección 'tasks'
            val documentReference = db.collection("users")
                .document(userId)
                .collection("tasks")
                .add(task)
                .await()  // Esto hace que la función espere hasta que se complete

            // Si la tarea se agregó correctamente, retorna el ID del documento
            Result.success(documentReference.id)
        } catch (e: Exception) {
            // Si hubo algún error, retorna el error
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
                .await()  // Espera hasta que se complete la operación de obtener las tareas

            // Mapear el resultado a una lista de objetos Task
            val tasks = snapshot.documents.map { it.toObject(Task::class.java)!! }
            Result.success(tasks)
        } catch (e: Exception) {
            // Si hubo un error, retorna el error
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
