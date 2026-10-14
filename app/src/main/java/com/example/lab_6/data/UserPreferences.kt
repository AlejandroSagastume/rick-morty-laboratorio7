package com.example.lab_6.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
    }

    val userNameFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[NAME_KEY]
        }

    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
        }
    }

    suspend fun clearName() {
        context.dataStore.edit { preferences ->
            preferences.remove(NAME_KEY)
        }
    }
}
