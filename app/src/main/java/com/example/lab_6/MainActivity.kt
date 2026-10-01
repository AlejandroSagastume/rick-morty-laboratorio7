package com.example.lab_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lab_6.data.CharacterDb
import com.example.lab_6.data.LocationDb
import com.example.lab_6.navigation.CharacterDetails
import com.example.lab_6.navigation.CharactersGraph
import com.example.lab_6.navigation.CharactersList
import com.example.lab_6.navigation.Login
import com.example.lab_6.navigation.LocationDetails
import com.example.lab_6.navigation.LocationsGraph
import com.example.lab_6.navigation.LocationsList
import com.example.lab_6.navigation.Main
import com.example.lab_6.navigation.Profile
import com.example.lab_6.screens.CharacterDetailsScreen
import com.example.lab_6.screens.CharactersScreen
import com.example.lab_6.screens.LoginScreen
import com.example.lab_6.screens.LocationDetailsScreen
import com.example.lab_6.screens.LocationsScreen
import com.example.lab_6.screens.ProfileScreen
import com.example.lab_6.ui.theme.LAB_6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LAB_6Theme {
                AppNavigation(
                    onExit = { finish() }
                )
            }
        }
    }
}

@Composable
private fun AppNavigation(
    onExit: () -> Unit
) {
    val navController = rememberNavController()
    val characterDb = remember { CharacterDb() }
    val locationDb = remember { LocationDb() }

    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                onStartClick = {
                    navController.navigate(Main) {
                        popUpTo<Login> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Main> {
            MainShell(
                characterDb = characterDb,
                locationDb = locationDb,
                onExit = onExit,
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

private enum class BottomDestination(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Characters("Characters", Icons.Default.Person),
    Locations("Locations", Icons.Default.LocationOn),
    Profile("Profile", Icons.Default.Person)
}

@Composable
private fun MainShell(
    characterDb: CharacterDb,
    locationDb: LocationDb,
    onExit: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = currentDestination?.hierarchy?.none { it.hasRoute<CharacterDetails>() || it.hasRoute<LocationDetails>() } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomDestination.entries.forEach { destination ->
                        val selected = when (destination) {
                            BottomDestination.Characters -> currentDestination?.hierarchy?.any { it.hasRoute<CharactersGraph>() } == true
                            BottomDestination.Locations -> currentDestination?.hierarchy?.any { it.hasRoute<LocationsGraph>() } == true
                            BottomDestination.Profile -> currentDestination?.hasRoute<Profile>() == true
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                when (destination) {
                                    BottomDestination.Characters -> navController.navigate(CharactersGraph) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    BottomDestination.Locations -> navController.navigate(LocationsGraph) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    BottomDestination.Profile -> navController.navigate(Profile) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CharactersGraph,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation<CharactersGraph>(startDestination = CharactersList) {
                composable<CharactersList> {
                    CharactersScreen(
                        characters = characterDb.getAllCharacters(),
                        onCharacterClick = { navController.navigate(CharacterDetails(it)) },
                        onExit = onExit
                    )
                }
                composable<CharacterDetails> { entry ->
                    CharacterDetailsScreen(
                        character = characterDb.getCharacterById(entry.toRoute<CharacterDetails>().id),
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            navigation<LocationsGraph>(startDestination = LocationsList) {
                composable<LocationsList> {
                    LocationsScreen(
                        locations = locationDb.getAllLocations(),
                        onLocationClick = { navController.navigate(LocationDetails(it)) }
                    )
                }
                composable<LocationDetails> { entry ->
                    LocationDetailsScreen(
                        location = locationDb.getLocationById(entry.toRoute<LocationDetails>().id),
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable<Profile> {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}