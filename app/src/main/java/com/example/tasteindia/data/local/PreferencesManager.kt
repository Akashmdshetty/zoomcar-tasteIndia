package com.example.tasteindia.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "taste_india_prefs")

interface PreferencesManager {
    val favouriteIds: Flow<Set<String>>
    suspend fun toggleFavourite(id: String)
}

class DefaultPreferencesManager(
    private val context: Context
) : PreferencesManager {

    companion object {
        private val FAVOURITE_IDS_KEY = stringSetPreferencesKey("favourite_meal_ids")
    }

    override val favouriteIds: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVOURITE_IDS_KEY] ?: emptySet()
        }

    override suspend fun toggleFavourite(id: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVOURITE_IDS_KEY] ?: emptySet()
            if (current.contains(id)) {
                preferences[FAVOURITE_IDS_KEY] = current - id
            } else {
                preferences[FAVOURITE_IDS_KEY] = current + id
            }
        }
    }
}
