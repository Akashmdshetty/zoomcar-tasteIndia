package com.example.tasteindia

import com.example.tasteindia.data.local.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakePreferencesManager : PreferencesManager {
    private val _favouritesFlow = MutableStateFlow<Set<String>>(emptySet())

    override val favouriteIds: Flow<Set<String>> = _favouritesFlow

    override suspend fun toggleFavourite(id: String) {
        val current = _favouritesFlow.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _favouritesFlow.value = current
    }
}

class FavouritesPersistenceTest {

    @Test
    fun favourites_addRemoveToggle_persistsCorrectlyOffline() = runBlocking {
        val prefsManager = FakePreferencesManager()

        // 1. Initial state is empty
        var favs = prefsManager.favouriteIds.first()
        assertTrue(favs.isEmpty())

        // 2. Add favourite "52850"
        prefsManager.toggleFavourite("52850")
        favs = prefsManager.favouriteIds.first()
        assertEquals(1, favs.size)
        assertTrue(favs.contains("52850"))

        // 3. Add second favourite "52785"
        prefsManager.toggleFavourite("52785")
        favs = prefsManager.favouriteIds.first()
        assertEquals(2, favs.size)
        assertTrue(favs.contains("52785"))

        // 4. Remove favourite "52850"
        prefsManager.toggleFavourite("52850")
        favs = prefsManager.favouriteIds.first()
        assertEquals(1, favs.size)
        assertFalse(favs.contains("52850"))
        assertTrue(favs.contains("52785"))
    }
}
