package myapp.chronify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import myapp.chronify.data.PreferencesKey
import myapp.chronify.data.PreferencesRepository
import myapp.chronify.data.schedule.ScheduleRepository

class SettingsUiState(
    val settings: Map<PreferencesKey<*>, Any> = emptyMap(),
    val isLoading: Boolean = true
)

class SettingsViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    /**
     * 偏好设置键列表
     */
    private val settingsKeys = listOf(
        PreferencesKey.DisplayPref.WeekStartFromSunday,
        PreferencesKey.DisplayPref.Theme,
        PreferencesKey.DisplayPref.BaseFontSize,
    )

    // val settings = preferencesRepository.getPreferences(settingsKeys)
    //     .map{ prefsMap  ->
    //         SettingsUiState(
    //             weekStartFromSunday = prefsMap[PreferencesKey.DisplayPref.WeekStartFromSunday] as Boolean,
    //             theme = prefsMap[PreferencesKey.DisplayPref.Theme] as String,
    //             baseFontSize = prefsMap[PreferencesKey.DisplayPref.BaseFontSize] as Int
    //         )
    //     }
    //     .stateIn(
    //         scope = viewModelScope,
    //         started = SharingStarted.WhileSubscribed(5000),
    //         initialValue = SettingsUiState()
    //     )

    // 组合加载状态和数据流
    val uiState: StateFlow<SettingsUiState> = preferencesRepository.getPreferences(settingsKeys)
        .map { prefsMap ->
            SettingsUiState(
                settings = prefsMap,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState(
                settings = buildMap {
                    settingsKeys.forEach { key ->
                        put(key, key.defaultValue)
                    }
                },
                isLoading = true
            )
        )

    /**
     * 偏好设置键值对
     */
    val settingsMap = preferencesRepository.getPreferences(settingsKeys)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            // 默认值不能为 emptyMap()，否则会导致null cannot be cast to non-null type
            initialValue = buildMap {
                settingsKeys.forEach { key ->
                    put(key, key.defaultValue)
                }
            }
        )

    /**
     * 更新偏好设置
     */
    suspend fun <T> updatePreference(key: PreferencesKey<T>, value: T) {
        preferencesRepository.updatePreference(key, value)
    }
}