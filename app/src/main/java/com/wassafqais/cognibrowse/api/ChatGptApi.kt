package com.wassafqais.cognibrowse.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatGptApi {
    @GET("chat/completions")
    fun sendMessage(
        @Query("apiKey") apiKey: String,
        @Query("message") message: String
    ): Call<ChatResponse>
}
