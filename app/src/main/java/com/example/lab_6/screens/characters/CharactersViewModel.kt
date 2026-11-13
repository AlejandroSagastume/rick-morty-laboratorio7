package com.example.lab_6.screens.characters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Character
import com.example.lab_6.data.local.entity.CharacterEntity
import com.example.lab_6.data.repository.RickAndMortyRepository
import com.example.lab_6.data.repository.RickAndMortyRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CharactersState(
    val isLoading: Boolean = true,
    val data: List<Character> = emptyList(),
    val hasError: Boolean = false,
    val showOnlyFavorites: Boolean = false
)

class CharactersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RickAndMortyRepository = RickAndMortyRepositoryImpl(application)
    private val _state = MutableStateFlow(CharactersState())
    val state: StateFlow<CharactersState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = CharactersState(isLoading = true)
            delay(4_000)
            try {
                val characters = repository.getCharactersList()
                _state.value = _state.value.copy(
                    isLoading = false,
                    data = characters,
                    hasError = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, hasError = true)
            }
        }
    }

    fun addCharacter(name: String, species: String, status: String, gender: String, image: String) {
        viewModelScope.launch {
            val newId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            val cleanImage = image.trim()
            val character = Character(
                id = newId,
                name = name,
                status = status,
                species = species,
                gender = gender,
                image = if (cleanImage.isBlank()) "https://rickandmortyapi.com/api/character/avatar/19.jpeg" else cleanImage,
                isFavorite = false
            )
            repository.insertCharacter(character)
            loadCharactersWithoutDelay()
        }
    }

    fun toggleFavorite(id: Int, currentFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateCharacterFavorite(id, !currentFavorite)
            loadCharactersWithoutDelay()
        }
    }

    private fun loadCharactersWithoutDelay() {
        viewModelScope.launch {
            try {
                val characters = repository.getCharactersList()
                _state.value = _state.value.copy(data = characters)
            } catch (e: Exception) {
            }
        }
    }

    fun toggleFilterFavorites() {
        _state.value = _state.value.copy(showOnlyFavorites = !_state.value.showOnlyFavorites)
    }

    fun onLoadingClicked() {
        if (_state.value.isLoading) {
            loadJob?.cancel()
            _state.value = _state.value.copy(isLoading = false, hasError = true)
        }
    }

    fun retry() {
        loadCharacters()
    }
}
