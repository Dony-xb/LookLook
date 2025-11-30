package com.looklook.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_TOKEN = stringPreferencesKey("token")

    suspend fun setToken(token: String?) {
        context.dataStore.edit { prefs: MutablePreferences ->
            if (token == null) {
                prefs.remove(KEY_TOKEN)
            } else {
                prefs[KEY_TOKEN] = token
            }
        }
    }

    suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_TOKEN]
    }
}

