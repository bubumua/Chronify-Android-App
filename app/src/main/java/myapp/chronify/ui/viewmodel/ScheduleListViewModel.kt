package myapp.chronify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.data.schedule.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import myapp.chronify.data.PreferencesRepository

data class ScheduleListUiState(val scheduleList: List<ScheduleEntity> = listOf())

// 枚举类表示筛选状态
enum class ScheduleFilter {
    UNFINISHED, FINISHED, ALL
}

class ScheduleListViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    // 添加当前筛选状态
    private val _currentFilter = MutableStateFlow(ScheduleFilter.UNFINISHED)
    val currentFilter = _currentFilter.asStateFlow()

    /**
     * Holds marker screen ui state. The list of schedules are retrieved from [ScheduleRepository] and mapped to [ScheduleListUiState]
     */
    val journalUiState: StateFlow<ScheduleListUiState> =
        combine(
            scheduleRepository.getUnfinishedSchedulesStream(),
            scheduleRepository.getFinishedSchedulesStream(),
            scheduleRepository.getAllSchedulesStream(),
            currentFilter
        ) { unfinished, finished, all, filter ->
            when (filter) {
                ScheduleFilter.UNFINISHED -> ScheduleListUiState(unfinished)
                ScheduleFilter.FINISHED -> ScheduleListUiState(finished)
                ScheduleFilter.ALL -> {
                    // 全部时，未完成的排在前面
                    ScheduleListUiState(unfinished + finished)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ScheduleListUiState()
        )

    // 添加更新筛选状态的方法
    fun updateFilter(filter: ScheduleFilter) {
        _currentFilter.value = filter
    }

    suspend fun deleteSchedule(schedule: ScheduleEntity) {
        scheduleRepository.delete(schedule)
    }

    suspend fun updateSchedule(schedule: ScheduleEntity) {
        scheduleRepository.update(schedule)
    }
}