package com.adasoraninda.githubuserdts.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppStorageManager private constructor(
    private val datastore: DataStore<Preferences>
) {

    companion object {
        const val DATA_STORE_NAME = "DATA_STORE"

        private const val KEY_THEMES = "KEY_THEMES"
        val PREF_KEY_THEMES = intPreferencesKey(KEY_THEMES)

        @Volatile
        private var INSTANCE: AppStorageManager? = null

        @JvmStatic
        fun getInstance(datastore: DataStore<Preferences>): AppStorageManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppStorageManager(datastore)
            }
        }
    }

    suspend fun <T> saveData(key: Preferences.Key<T>, value: T) {
        datastore.edit { settings ->
            settings[key] = value
        }
    }

    fun <T> getData(key: Preferences.Key<T>): Flow<T?> {
        return datastore.data.map { prefs ->
            prefs[key]
        }
    }

}