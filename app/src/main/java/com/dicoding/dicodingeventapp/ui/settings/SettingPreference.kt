package com.dicoding.dicodingeventapp.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val themeKey = booleanPreferencesKey("theme_setting")
    private val notificationKey = booleanPreferencesKey("notification_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preference ->
            preference[themeKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preference ->
            preference[themeKey] = isDarkModeActive
        }
    }

    fun getNotificationSetting(): Flow<Boolean> {
        return dataStore.data.map { preference ->
            preference[notificationKey] ?: false
        }
    }

    suspend fun saveNotificationSetting(isNotificationActive: Boolean) {
        dataStore.edit { preference ->
            preference[notificationKey] = isNotificationActive
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingsPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}