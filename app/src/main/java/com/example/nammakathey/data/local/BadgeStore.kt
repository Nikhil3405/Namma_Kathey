package com.example.nammakathey.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "badge_prefs")

object BadgeStore {

    private val COMPLETED_HEROES_KEY = stringSetPreferencesKey("completed_heroes")

    fun getCompletedHeroes(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { prefs ->
            prefs[COMPLETED_HEROES_KEY] ?: emptySet()
        }
    }

    suspend fun addHero(context: Context, heroId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[COMPLETED_HEROES_KEY] ?: emptySet()
            prefs[COMPLETED_HEROES_KEY] = current + heroId
        }
    }
}