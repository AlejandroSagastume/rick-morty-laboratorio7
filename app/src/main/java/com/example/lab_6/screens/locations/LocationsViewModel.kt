package com.example.lab_6.screens.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_6.data.Location
import com.example.lab_6.data.local.AppDatabase
import com.example.lab_6.data.mapper.toModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LocationFilter {
    ALL, FAVORITES, NON_FAVORITES
}

data class LocationsState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false,
    val filter: LocationFilter = LocationFilter.ALL
)

class LocationsViewModel(application: Application) : AndroidViewModel(application) {
    private val locationDao = AppDatabase.getDatabase(application).locationDao()
    private val _state = MutableStateFlow(LocationsState())
    val state: StateFlow<LocationsState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        loadLocations()
    }

    private fun loadLocations() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = LocationsState(isLoading = true)
            delay(4_000)
            try {
                locationDao.getAllLocations().collect { entities ->
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

    fun toggleFavorite(id: Int, currentFavorite: Boolean) {
        viewModelScope.launch {
            locationDao.updateFavorite(id, !currentFavorite)
        }
    }

    fun cycleFilter() {
        val nextFilter = when (_state.value.filter) {
            LocationFilter.ALL -> LocationFilter.FAVORITES
            LocationFilter.FAVORITES -> LocationFilter.NON_FAVORITES
            LocationFilter.NON_FAVORITES -> LocationFilter.ALL
        }
        _state.value = _state.value.copy(filter = nextFilter)
    }

    fun onLoadingClicked() {
        if (_state.value.isLoading) {
            loadJob?.cancel()
            _state.value = _state.value.copy(isLoading = false, hasError = true)
        }
    }

    fun retry() {
        loadLocations()
    }
}
