package myapp.chronify.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// update when migrating to a new version
const val CURRENT_PREFERENCES_VERSION = 1

sealed class PreferencesKey<T> {
    abstract val key: Preferences.Key<T>
    abstract val defaultValue: T  // 添加默认值属性

    sealed class DisplayPref {
        object WeekStartFromSunday : PreferencesKey<Boolean>() {
            override val key = booleanPreferencesKey("display_pref_week_start_from_sunday")
            override val defaultValue = false
        }
        object Theme : PreferencesKey<String>() {
            override val key = stringPreferencesKey("app_settings_theme")
            override val defaultValue = "bluesimple"
        }
        object BaseFontSize : PreferencesKey<Int>() {
            override val key = intPreferencesKey("app_settings_base_font_size")
            override val defaultValue = 14
        }
    }

    sealed class AppSettings {
        object Version : PreferencesKey<Int>() {
            override val key = intPreferencesKey("app_settings_version")
            override val defaultValue = 0
        }
        object IsFirstLaunch : PreferencesKey<Boolean>() {
            override val key = booleanPreferencesKey("app_settings_first_launch")
            override val defaultValue = true
        }
    }
    // ... 其他偏好设置组
}


class PreferencesRepository(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "Chronify_preferences")

    // 初始化所有偏好设置
    suspend fun initializePreferences() {
        // 如果某个偏好设置不存在，则设置默认值
        context.dataStore.edit { preferences ->
            if (!preferences.contains(PreferencesKey.DisplayPref.WeekStartFromSunday.key)) {
                preferences[PreferencesKey.DisplayPref.WeekStartFromSunday.key] = PreferencesKey.DisplayPref.WeekStartFromSunday.defaultValue
            }
            if (!preferences.contains(PreferencesKey.AppSettings.IsFirstLaunch.key)) {
                preferences[PreferencesKey.AppSettings.IsFirstLaunch.key] = PreferencesKey.AppSettings.IsFirstLaunch.defaultValue
            }
            if (!preferences.contains(PreferencesKey.DisplayPref.Theme.key)) {
                preferences[PreferencesKey.DisplayPref.Theme.key] = PreferencesKey.DisplayPref.Theme.defaultValue
            }
            if (!preferences.contains(PreferencesKey.DisplayPref.BaseFontSize.key)) {
                preferences[PreferencesKey.DisplayPref.BaseFontSize.key] = PreferencesKey.DisplayPref.BaseFontSize.defaultValue
            }
            // ... 其他偏好设置的初始化
        }
    }

    // 重置所有偏好设置到默认值
    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            // 清除所有现有偏好
            preferences.clear()
            // 重新设置默认值
            preferences[PreferencesKey.DisplayPref.WeekStartFromSunday.key] = PreferencesKey.DisplayPref.WeekStartFromSunday.defaultValue
            preferences[PreferencesKey.DisplayPref.Theme.key] = PreferencesKey.DisplayPref.Theme.defaultValue
            preferences[PreferencesKey.DisplayPref.BaseFontSize.key] = PreferencesKey.DisplayPref.BaseFontSize.defaultValue
            preferences[PreferencesKey.AppSettings.IsFirstLaunch.key] = PreferencesKey.AppSettings.IsFirstLaunch.defaultValue
            // ... 其他偏好设置的重置
        }
    }

    // 获取单个偏好设置
    fun <T> getPreference(preferencesKey: PreferencesKey<T>): Flow<T> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("UserPreferencesRepo", "Error reading preferences.", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[preferencesKey.key] ?: preferencesKey.defaultValue
            }
    }

    // 获取多个相关偏好设置
    fun getPreferences(keys: List<PreferencesKey<*>>): Flow<Map<PreferencesKey<*>, Any>> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("UserPreferencesRepo", "Error reading preferences.", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                buildMap {
                    keys.forEach { key ->
                        // 使用defaultValue确保类型安全
                        (preferences[key.key] ?: key.defaultValue)?.let { put(key, it) }
                    }
                }
            }
    }

    // 更新单个偏好设置
    suspend fun <T> updatePreference(preferencesKey: PreferencesKey<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[preferencesKey.key] = value
        }
    }

    // 批量更新偏好设置
    suspend fun <T> updatePreferences(updates: Map<PreferencesKey<T>, T>) {
        context.dataStore.edit { preferences ->
            updates.forEach { (key, value) ->
                preferences[key.key] = value
            }
        }
    }

    private suspend fun updatePreferencesIfNeeded() {
        val currentVersion = getPreference(PreferencesKey.AppSettings.Version).first()

        if (currentVersion < CURRENT_PREFERENCES_VERSION) {
            when (currentVersion) {
                0 -> migrateFromV0ToV1()
                // 添加其他版本的迁移逻辑
            }

            // 更新版本号
            updatePreference(PreferencesKey.AppSettings.Version, CURRENT_PREFERENCES_VERSION)
        }
    }

    private suspend fun migrateFromV0ToV1() {
        // 执行从版本0到版本1的迁移逻辑
        context.dataStore.edit { preferences ->
            // 例如：重命名某个key、更改数据结构等
        }
    }
}