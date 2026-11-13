package com.example.lab_6.data.repository

import com.example.lab_6.data.Character
import com.example.lab_6.data.Location
import kotlinx.coroutines.flow.Flow

interface RickAndMortyRepository {
    val charactersFlow: Flow<List<Character>>
    val locationsFlow: Flow<List<Location>>
    suspend fun getCharactersList(): List<Character>
    suspend fun getLocationsList(): List<Location>
    suspend fun syncWithApiIfNeeded()
    suspend fun insertCharacter(character: Character)
    suspend fun updateCharacter(character: Character)
    suspend fun deleteCharacter(id: Int)
    suspend fun updateCharacterFavorite(id: Int, isFavorite: Boolean)
    suspend fun updateLocationFavorite(id: Int, isFavorite: Boolean)
    suspend fun clearAllData()
}
