package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID


class TaskRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función suspendida para agregar una tarea
    suspend fun addTask(userId: String, task: Task): Result<String> {
        return try {
            val documentReference = db.collection("users")
                .document(userId)
                .collection("tasks")
                .add(task)  // Usamos `add()` para que Firestore genere el ID automáticamente
                .await()

            // Obtener el ID generado por Firestore
            val generatedId = documentReference.id

            // Crear una nueva tarea con el ID generado
            val taskWithId = task.copy(id = generatedId)

            // Guardamos la tarea con el ID generado dentro del documento
            documentReference.set(taskWithId).await()

            // Retornamos el ID generado
            Result.success(generatedId)
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
            // Actualizamos el documento específico con el ID proporcionado
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)  // Usamos el ID de la tarea
                .set(updatedTask)  // Usamos merge para no sobrescribir todo el documento
                .await()

            // Retornamos un resultado exitoso
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)  // Si ocurre un error, lo capturamos y lo retornamos
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

    // Función para actualizar el campo 'completed' de la tarea en Firestore
    suspend fun updateTaskCompletion(userId: String, taskId: String, isCompleted: Boolean) {
        try {
            val taskRef = db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)

            taskRef.update("completed", isCompleted).await()  // Actualiza el campo 'completed'
        } catch (e: Exception) {
            throw Exception("Error al actualizar el estado de la tarea: ${e.message}")
        }
    }

}
