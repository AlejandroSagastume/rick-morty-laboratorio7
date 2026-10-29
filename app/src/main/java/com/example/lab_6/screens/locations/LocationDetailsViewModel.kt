package com.example.lab_6.screens.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Location
import com.example.lab_6.data.local.AppDatabase
import com.example.lab_6.data.mapper.toModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationDetailsState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)

class LocationDetailsViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val locationDao = AppDatabase.getDatabase(application).locationDao()
    private val id: Int = checkNotNull(savedStateHandle.get<Int>("id"))
    private val _state = MutableStateFlow(LocationDetailsState())
    val state: StateFlow<LocationDetailsState> = _state.asStateFlow()

    init {
        loadLocation()
    }

    private fun loadLocation() {
        viewModelScope.launch {
            _state.value = LocationDetailsState(isLoading = true)
            val entity = runCatching { locationDao.getLocationById(id) }.getOrNull()
            if (entity != null) {
                _state.value = LocationDetailsState(isLoading = false, data = entity.toModel())
            } else {
                _state.value = LocationDetailsState(isLoading = false, hasError = true)
            }
        }
    }

    fun onLoadingClicked() {
        if (_state.value.isLoading) {
            _state.value = _state.value.copy(isLoading = false, hasError = true)
        }
    }

    fun retry() {
        loadLocation()
    }
}
