package eriksu.commercial.rentingproject.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "app_preferences")

class DataStoreHelper(private val context: Context) {

    private val FIRST_TIME_KEY = booleanPreferencesKey("first_time")

    val isFirstTime = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[FIRST_TIME_KEY] ?: true
        }

    suspend fun setFirstTime(isFirstTime: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_TIME_KEY] = isFirstTime
        }
    }
}