package com.example.lab_6.screens.locations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Location
import com.example.lab_6.data.LocationDb
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationDetailsState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)

class LocationDetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val db = LocationDb()
    private val id: Int = checkNotNull(savedStateHandle.get<Int>("id"))
    private val _state = MutableStateFlow(LocationDetailsState())
    val state: StateFlow<LocationDetailsState> = _state.asStateFlow()
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
            _state.value = LocationDetailsState()
            delay(2_000)
            val location = runCatching { db.getLocationById(id) }.getOrNull()
            _state.value = LocationDetailsState(
                isLoading = false,
                data = location,
                hasError = location == null
            )
        }
    }
}
