package com.adasoraninda.githubuserdts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adasoraninda.githubuserdts.local.AppStorageManager
import com.adasoraninda.githubuserdts.utils.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

private const val DELAY_DURATION = 2_000L

class SplashViewModel(
    private val appStorageManager: AppStorageManager
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private val _navigation = MutableLiveData<Event<Boolean>>()
    val navigation: LiveData<Event<Boolean>> get() = _navigation

    private val _themes = MutableLiveData<Int?>()
    val themes: LiveData<Int?> get() = _themes

    init {
        getThemes()
        doNavigation()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    private fun doNavigation() {
        coroutineScope.launch {
            delay(DELAY_DURATION)
            _navigation.value = Event(true)
        }
    }

    private fun getThemes() {
        coroutineScope.launch {
            appStorageManager.getData(AppStorageManager.PREF_KEY_THEMES).collect {
                _themes.value = it
            }
        }
    }

}