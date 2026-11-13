package com.example.lab_6.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RickAndMortyApiService {
    @GET("character")
    suspend fun getCharacters(): CharacterApiResponse

    @GET("location")
    suspend fun getLocations(): LocationApiResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    val apiService: RickAndMortyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyApiService::class.java)
    }
}
