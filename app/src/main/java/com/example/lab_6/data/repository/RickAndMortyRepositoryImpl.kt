package com.example.lab_6.data.repository

import android.content.Context
import com.example.lab_6.data.Character
import com.example.lab_6.data.CharacterDb
import com.example.lab_6.data.Location
import com.example.lab_6.data.LocationDb
import com.example.lab_6.data.local.AppDatabase
import com.example.lab_6.data.local.entity.CharacterEntity
import com.example.lab_6.data.local.entity.LocationEntity
import com.example.lab_6.data.mapper.toEntity
import com.example.lab_6.data.mapper.toModel
import com.example.lab_6.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RickAndMortyRepositoryImpl(context: Context) : RickAndMortyRepository {
    private val database = AppDatabase.getDatabase(context)
    private val characterDao = database.characterDao()
    private val locationDao = database.locationDao()
    private val api = RetrofitClient.apiService

    override val charactersFlow: Flow<List<Character>> = characterDao.getAllCharacters()
        .map { entities -> entities.map { it.toModel() } }

    override val locationsFlow: Flow<List<Location>> = locationDao.getAllLocations()
        .map { entities -> entities.map { it.toModel() } }

    override suspend fun getCharactersList(): List<Character> {
        return withContext(Dispatchers.IO) {
            characterDao.getAllCharactersList().map { it.toModel() }
        }
    }

    override suspend fun getLocationsList(): List<Location> {
        return withContext(Dispatchers.IO) {
            locationDao.getAllLocationsList().map { it.toModel() }
        }
    }

    override suspend fun syncWithApiIfNeeded() {
        withContext(Dispatchers.IO) {
            if (characterDao.getCount() == 0) {
                var success = false
                try {
                    val charResponse = api.getCharacters()
                    val locResponse = api.getLocations()

                    if (charResponse.results.isNotEmpty()) {
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
                                dimension = it.dimension,
                                isFavorite = false
                            )
                        }

                        characterDao.insertAll(characterEntities)
                        locationDao.insertAll(locationEntities)
                        success = true
                    }
                } catch (t: Throwable) {
                    success = false
                }

                if (!success) {
                    val charDb = CharacterDb()
                    val locDb = LocationDb()
                    characterDao.insertAll(charDb.getAllCharacters().map { it.toEntity() })
                    locationDao.insertAll(locDb.getAllLocations().map { it.toEntity() })
                }
            }
        }
    }

    override suspend fun insertCharacter(character: Character) {
        characterDao.insert(character.toEntity())
    }

    override suspend fun updateCharacter(character: Character) {
        characterDao.update(character.toEntity())
    }

    override suspend fun deleteCharacter(id: Int) {
        characterDao.deleteById(id)
    }

    override suspend fun updateCharacterFavorite(id: Int, isFavorite: Boolean) {
        characterDao.updateFavorite(id, isFavorite)
    }

    override suspend fun updateLocationFavorite(id: Int, isFavorite: Boolean) {
        locationDao.updateFavorite(id, isFavorite)
    }

    override suspend fun clearAllData() {
        characterDao.deleteAll()
        locationDao.deleteAll()
    }
}
