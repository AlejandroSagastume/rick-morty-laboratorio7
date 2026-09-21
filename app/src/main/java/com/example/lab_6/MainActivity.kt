package com.example.lab_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lab_6.data.CharacterDb
import com.example.lab_6.navigation.CharacterDetails
import com.example.lab_6.navigation.Characters
import com.example.lab_6.navigation.Login
import com.example.lab_6.screens.CharacterDetailsScreen
import com.example.lab_6.screens.CharactersScreen
import com.example.lab_6.screens.LoginScreen
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

    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                onStartClick = {
                    navController.navigate(Characters) {
                        popUpTo<Login> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Characters> {
            CharactersScreen(
                characters = characterDb.getAllCharacters(),
                onCharacterClick = { characterId ->
                    navController.navigate(CharacterDetails(characterId))
                },
                onExit = onExit
            )
        }

        composable<CharacterDetails> { backStackEntry ->
            val destination = backStackEntry.toRoute<CharacterDetails>()
            val character = characterDb.getCharacterById(destination.id)

            CharacterDetailsScreen(
                character = character,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}