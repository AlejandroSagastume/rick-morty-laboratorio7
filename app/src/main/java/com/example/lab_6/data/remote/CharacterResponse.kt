package com.example.lab_6.data.remote

data class CharacterApiResponse(
    val results: List<ApiCharacter>
)

data class ApiCharacter(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)
