package com.example.lab_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.lab_6.data.UserPreferences
import com.example.lab_6.navigation.CharacterDetails
import com.example.lab_6.navigation.CharactersGraph
import com.example.lab_6.navigation.CharactersList
import com.example.lab_6.navigation.Login
import com.example.lab_6.navigation.LocationDetails
import com.example.lab_6.navigation.LocationsGraph
import com.example.lab_6.navigation.LocationsList
import com.example.lab_6.navigation.Main
import com.example.lab_6.navigation.Profile
import com.example.lab_6.screens.characters.CharacterDetailsScreen
import com.example.lab_6.screens.characters.CharactersScreen
import com.example.lab_6.screens.login.LoginScreen
import com.example.lab_6.screens.locations.LocationDetailsScreen
import com.example.lab_6.screens.locations.LocationsScreen
import com.example.lab_6.screens.profile.ProfileScreen
import com.example.lab_6.ui.theme.LAB_6Theme
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    val savedName by produceState(initialValue = "loading", userPreferences) {
        value = userPreferences.userNameFlow.firstOrNull() ?: ""
    }

    if (savedName == "loading") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDest = if (savedName.isNotBlank()) Main else Login

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable<Login> {
            LoginScreen(
                onStartClick = { name ->
                    coroutineScope.launch {
                        userPreferences.saveName(name)
                        navController.navigate(Main) {
                            popUpTo<Login> {
                                inclusive = true
                            }
                        }
                    }
                },
                onExit = onExit
            )
        }

        composable<Main> {
            MainShell(
                userPreferences = userPreferences,
                onExit = onExit,
                onLogout = {
                    coroutineScope.launch {
                        userPreferences.clearName()
                        navController.navigate(Login) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
    }
}

private enum class BottomDestination(
    val label: String,
    val icon: ImageVector
) {
    Characters("Characters", Icons.Default.Person),
    Locations("Locations", Icons.Default.LocationOn),
    Profile("Profile", Icons.Default.Person)
}

@Composable
private fun MainShell(
    userPreferences: UserPreferences,
    onExit: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = currentDestination?.hierarchy?.none { it.hasRoute<CharacterDetails>() || it.hasRoute<LocationDetails>() } ?: true
    val userName by userPreferences.userNameFlow.collectAsState(initial = "")

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
                        onCharacterClick = { navController.navigate(CharacterDetails(it)) },
                        onExit = onExit
                    )
                }
                composable<CharacterDetails> {
                    CharacterDetailsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            navigation<LocationsGraph>(startDestination = LocationsList) {
                composable<LocationsList> {
                    LocationsScreen(
                        onLocationClick = { navController.navigate(LocationDetails(it)) }
                    )
                }
                composable<LocationDetails> {
                    LocationDetailsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable<Profile> {
                ProfileScreen(
                    userName = userName ?: "",
                    onLogout = onLogout
                )
            }
        }
    }
}
