package com.example.lab_6.screens.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab_6.data.Location
import com.example.lab_6.screens.common.ErrorLayout
import com.example.lab_6.screens.common.LoadingLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    onLocationClick: (Int) -> Unit,
    viewModel: LocationsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val displayList = when (state.filter) {
        LocationFilter.ALL -> state.data
        LocationFilter.FAVORITES -> state.data.filter { it.isFavorite }
        LocationFilter.NON_FAVORITES -> state.data.filter { !it.isFavorite }
    }

    val filterTitle = when (state.filter) {
        LocationFilter.ALL -> "Locations"
        LocationFilter.FAVORITES -> "Favorite Locations"
        LocationFilter.NON_FAVORITES -> "Non-Favorite Locations"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filterTitle, color = MaterialTheme.colorScheme.onPrimary) },
                actions = {
                    IconButton(onClick = { viewModel.cycleFilter() }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Filter locations",
                            tint = if (state.filter != LocationFilter.ALL) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingLayout(
                modifier = Modifier.padding(innerPadding),
                onClick = viewModel::onLoadingClicked
            )
            state.hasError -> ErrorLayout(
                message = "Error al obtener listado de ubicaciones.",
                modifier = Modifier.padding(innerPadding),
                onRetry = viewModel::retry
            )
            displayList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay elementos a mostrar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayList, key = { it.id }) { location ->
                    LocationCard(
                        location = location,
                        onClick = { onLocationClick(location.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(location.id, location.isFavorite) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationCard(
    location: Location,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(location.name, style = MaterialTheme.typography.titleMedium)
            Text(
                location.type,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (location.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (location.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
