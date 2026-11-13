package com.example.lab_6.screens.characters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Character
import com.example.lab_6.data.local.AppDatabase
import com.example.lab_6.data.local.entity.CharacterEntity
import com.example.lab_6.data.mapper.toModel
import com.example.lab_6.data.repository.RickAndMortyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CharacterDetailsState(
    val isLoading: Boolean = true,
    val data: Character? = null,
    val hasError: Boolean = false
)

class CharacterDetailsViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val repository = RickAndMortyRepository(application)
    private val characterDao = AppDatabase.getDatabase(application).characterDao()
    private val id: Int = checkNotNull(savedStateHandle.get<Int>("id"))
    private val _state = MutableStateFlow(CharacterDetailsState())
    val state: StateFlow<CharacterDetailsState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        loadCharacter()
    }

    private fun loadCharacter() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = CharacterDetailsState(isLoading = true)
            delay(2_000)
            val entity = runCatching { characterDao.getCharacterById(id) }.getOrNull()
            if (entity != null) {
                _state.value = CharacterDetailsState(isLoading = false, data = entity.toModel())
            } else {
                _state.value = CharacterDetailsState(isLoading = false, hasError = true)
            }
        }
    }

    fun updateCharacter(name: String, species: String, status: String, gender: String) {
        val current = _state.value.data
        if (current != null) {
            viewModelScope.launch {
                val entity = CharacterEntity(
                    id = current.id,
                    name = name,
                    status = status,
                    species = species,
                    gender = gender,
                    image = current.image,
                    isFavorite = current.isFavorite
                )
                repository.updateCharacter(entity)
                _state.value = _state.value.copy(data = entity.toModel())
            }
        }
    }

    fun deleteCharacter(onDeleted: () -> Unit) {
        val current = _state.value.data
        if (current != null) {
            viewModelScope.launch {
                repository.deleteCharacter(current.id)
                onDeleted()
            }
        }
    }

    fun toggleFavorite() {
        val current = _state.value.data
        if (current != null) {
            viewModelScope.launch {
                val newFavorite = !current.isFavorite
                repository.updateCharacterFavorite(id, newFavorite)
                _state.value = _state.value.copy(data = current.copy(isFavorite = newFavorite))
            }
        }
    }

    fun onLoadingClicked() {
        if (_state.value.isLoading) {
            loadJob?.cancel()
            _state.value = _state.value.copy(isLoading = false, hasError = true)
        }
    }

    fun retry() {
        loadCharacter()
    }
}
