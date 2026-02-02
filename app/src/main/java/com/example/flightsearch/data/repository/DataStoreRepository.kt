package com.example.flightsearch.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreRepository(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    override val prevRequest: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferencies")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[PREV_REQUEST] ?: ""
        }

    override suspend fun saveRequest(request: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PREV_REQUEST] = request
            }
            Log.d(TAG, "Request saved: $request")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save request: $request", e)
            throw e
        }
    }

    private companion object {
        val PREV_REQUEST = stringPreferencesKey("prev_request")
        val TAG = "UserPreferencesRepo"
    }
}