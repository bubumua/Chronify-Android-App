package myapp.chronify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.data.schedule.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted

data class RemindUiState(val scheduleList: List<ScheduleEntity> = listOf())

class RemindViewModel(val scheduleRepository: ScheduleRepository) : ViewModel() {


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    /**
     * Holds mark screen ui state. The list of schedules are retrieved from [ScheduleRepository] and mapped to [RemindUiState]
     */
    val remindUiState: StateFlow<RemindUiState> =
        scheduleRepository.getAllSchedulesStream().map { RemindUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = RemindUiState()
        )

    suspend fun deleteSchedule(schedule: ScheduleEntity){
        scheduleRepository.delete(schedule)
    }
}