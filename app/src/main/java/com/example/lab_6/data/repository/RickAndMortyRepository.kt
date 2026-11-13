package com.example.lab_6.data.repository

import android.content.Context
import com.example.lab_6.data.local.AppDatabase
import com.example.lab_6.data.local.entity.CharacterEntity
import com.example.lab_6.data.local.entity.LocationEntity
import com.example.lab_6.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class RickAndMortyRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val characterDao = database.characterDao()
    private val locationDao = database.locationDao()

    val charactersFlow: Flow<List<CharacterEntity>> = characterDao.getAllCharacters()
    val locationsFlow: Flow<List<LocationEntity>> = locationDao.getAllLocations()

    suspend fun syncWithApiIfNeeded() {
        if (characterDao.getCount() == 0) {
            try {
                val charResponse = RetrofitClient.apiService.getCharacters()
                val locResponse = RetrofitClient.apiService.getLocations()

                val characterEntities = charResponse.results.map {
                    CharacterEntity(
                        id = it.id,
                        name = it.name,
                        status = it.status,
                        species = it.species,
                        gender = it.gender,
                        image = it.image,
                        isFavorite = false
                    )
                }
                val locationEntities = locResponse.results.map {
                    LocationEntity(
                        id = it.id,
                        name = it.name,
                        type = it.type,
                        dimension = it.dimension
                    )
                }

                characterDao.insertAll(characterEntities)
                locationDao.insertAll(locationEntities)
            } catch (e: Exception) {
            }
        }
    }

    suspend fun insertCharacter(character: CharacterEntity) {
        characterDao.insert(character)
    }

    suspend fun updateCharacter(character: CharacterEntity) {
        characterDao.update(character)
    }

    suspend fun deleteCharacter(id: Int) {
        characterDao.deleteById(id)
    }

    suspend fun updateCharacterFavorite(id: Int, isFavorite: Boolean) {
        characterDao.updateFavorite(id, isFavorite)
    }

    suspend fun updateLocationFavorite(id: Int, isFavorite: Boolean) {
        locationDao.updateFavorite(id, isFavorite)
    }

    suspend fun clearAllData() {
        characterDao.deleteAll()
        locationDao.deleteAll()
    }
}
