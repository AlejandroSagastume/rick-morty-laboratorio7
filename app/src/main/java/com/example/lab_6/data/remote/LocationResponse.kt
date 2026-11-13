package com.example.lab_6.data.remote

data class LocationApiResponse(
    val results: List<ApiLocation>
)

data class ApiLocation(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)
