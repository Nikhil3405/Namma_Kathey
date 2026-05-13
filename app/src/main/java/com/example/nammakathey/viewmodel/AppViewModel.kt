package com.example.nammakathey.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakathey.data.local.BadgeStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val context = getApplication<Application>()

    val completedHeroes: StateFlow<Set<String>> =
        BadgeStore.getCompletedHeroes(context)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )

    fun markHeroCompleted(heroId: String) {
        viewModelScope.launch {
            BadgeStore.addHero(context, heroId)
        }
    }
}