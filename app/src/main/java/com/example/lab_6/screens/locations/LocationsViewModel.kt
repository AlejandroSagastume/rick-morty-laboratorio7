package com.example.lab_6.screens.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Location
import com.example.lab_6.data.local.AppDatabase
import com.example.lab_6.data.mapper.toModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LocationsState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false
)

class LocationsViewModel(application: Application) : AndroidViewModel(application) {
    private val locationDao = AppDatabase.getDatabase(application).locationDao()
    private val _state = MutableStateFlow(LocationsState())
    val state: StateFlow<LocationsState> = _state.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                locationDao.getAllLocations().collectLatest { entities ->
                    val locations = entities.map { it.toModel() }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        data = locations,
                        hasError = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, hasError = true)
            }
        }
    }

    fun onLoadingClicked() {
        if (_state.value.isLoading) {
            _state.value = _state.value.copy(isLoading = false, hasError = true)
        }
    }

    fun retry() {
        loadLocations()
    }
}
