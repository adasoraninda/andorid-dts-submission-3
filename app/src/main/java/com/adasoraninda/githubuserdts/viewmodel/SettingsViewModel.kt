package com.adasoraninda.githubuserdts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adasoraninda.githubuserdts.local.AppStorageManager
import com.adasoraninda.githubuserdts.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appStorageManager: AppStorageManager
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private val _themes = MutableLiveData<Int?>()
    val themes: LiveData<Int?> get() = _themes

    private val _back = MutableLiveData<Event<Boolean>>()
    val back: LiveData<Event<Boolean>> get() = _back

    init {
        getThemes()
    }

    fun saveThemes(value: Int) {
        coroutineScope.launch {
            appStorageManager.saveData(AppStorageManager.PREF_KEY_THEMES, value)
        }
    }

    fun onBackClicked() {
        _back.value = Event(true)
    }


    private fun getThemes() {
        coroutineScope.launch {
            appStorageManager.getData(AppStorageManager.PREF_KEY_THEMES).collect {
                _themes.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

}