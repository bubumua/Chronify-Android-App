package myapp.chronify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.data.schedule.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

data class ScheduleListUiState(val scheduleList: List<ScheduleEntity> = listOf())

data class HistoryUiState(val scheduleList: List<ScheduleEntity> = listOf())

// 添加一个枚举类表示筛选状态
enum class ScheduleFilter {
    UNFINISHED, FINISHED, ALL
}

class ScheduleListViewModel(val scheduleRepository: ScheduleRepository) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // 添加当前筛选状态
    private val _currentFilter = MutableStateFlow(ScheduleFilter.UNFINISHED)
    val currentFilter = _currentFilter.asStateFlow()

    // 修改 scheduleListUiState 的实现
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

    /**
     * Holds mark screen ui state. The list of schedules are retrieved from [ScheduleRepository] and mapped to [ScheduleListUiState]
     */
    val scheduleListUiState: StateFlow<ScheduleListUiState> =
        scheduleRepository.getUnfinishedSchedulesStream().map { ScheduleListUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ScheduleListUiState()
        )

    val historyUiState: StateFlow<HistoryUiState> =
        scheduleRepository.getFinishedSchedulesStream().map { HistoryUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(myapp.chronify.ui.viewmodel.TIMEOUT_MILLIS),
            initialValue = HistoryUiState()
        )

    suspend fun deleteSchedule(schedule: ScheduleEntity) {
        scheduleRepository.delete(schedule)
    }

    suspend fun updateSchedule(schedule: ScheduleEntity) {
        scheduleRepository.update(schedule)
    }
}