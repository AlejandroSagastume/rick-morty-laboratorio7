package com.example.lab_6.screens.characters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Character
import com.example.lab_6.data.local.AppDatabase
import com.example.lab_6.data.mapper.toModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CharactersState(
    val isLoading: Boolean = true,
    val data: List<Character> = emptyList(),
    val hasError: Boolean = false,
    val showOnlyFavorites: Boolean = false
)

class CharactersViewModel(application: Application) : AndroidViewModel(application) {
    private val characterDao = AppDatabase.getDatabase(application).characterDao()
    private val _state = MutableStateFlow(CharactersState())
    val state: StateFlow<CharactersState> = _state.asStateFlow()

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                characterDao.getAllCharacters().collectLatest { entities ->
                    val characters = entities.map { it.toModel() }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        data = characters,
                        hasError = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, hasError = true)
            }
        }
    }

    fun toggleFavorite(id: Int, currentFavorite: Boolean) {
        viewModelScope.launch {
            characterDao.updateFavorite(id, !currentFavorite)
        }
    }

    fun toggleFilterFavorites() {
        _state.value = _state.value.copy(showOnlyFavorites = !_state.value.showOnlyFavorites)
    }

    fun onLoadingClicked() {
        if (_state.value.isLoading) {
            _state.value = _state.value.copy(isLoading = false, hasError = true)
        }
    }

    fun retry() {
        loadCharacters()
    }
}
