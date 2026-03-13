package com.labguardian.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val STATION_ID = stringPreferencesKey("station_id")
        val STUDENT_NAME = stringPreferencesKey("student_name")
        val SERVER_URL = stringPreferencesKey("server_url")
    }

    val stationId: Flow<String> = context.dataStore.data.map { it[Keys.STATION_ID] ?: "" }
    val studentName: Flow<String> = context.dataStore.data.map { it[Keys.STUDENT_NAME] ?: "" }
    val serverUrl: Flow<String> = context.dataStore.data.map { it[Keys.SERVER_URL] ?: "" }

    suspend fun setStationId(id: String) {
        context.dataStore.edit { it[Keys.STATION_ID] = id }
    }

    suspend fun setStudentName(name: String) {
        context.dataStore.edit { it[Keys.STUDENT_NAME] = name }
    }

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[Keys.SERVER_URL] = url }
    }
}
