package com.example.tfgonitime.ia.api

import com.example.tfgonitime.ia.model.ChatRequest
import com.example.tfgonitime.ia.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun sendChat(
        @Body request: ChatRequest
    ): retrofit2.Response<ChatResponse>
}

