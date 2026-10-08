package com.example.lab_6.screens.characters

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Character
import com.example.lab_6.data.CharacterDb
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

class CharacterDetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val db = CharacterDb()
    private val id: Int = checkNotNull(savedStateHandle.get<Int>("id"))
    private val _state = MutableStateFlow(CharacterDetailsState())
    val state: StateFlow<CharacterDetailsState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        load()
    }

    fun onLoadingClicked() {
        if (_state.value.isLoading) {
            loadJob?.cancel()
            _state.value = _state.value.copy(isLoading = false, hasError = true)
        }
    }

    fun retry() = load()

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = CharacterDetailsState()
            delay(2_000)
            val character = runCatching { db.getCharacterById(id) }.getOrNull()
            _state.value = CharacterDetailsState(
                isLoading = false,
                data = character,
                hasError = character == null
            )
        }
    }
}
