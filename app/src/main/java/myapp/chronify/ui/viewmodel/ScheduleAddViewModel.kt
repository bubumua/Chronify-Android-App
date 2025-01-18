package myapp.chronify.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import myapp.chronify.data.schedule.ScheduleRepository
import myapp.chronify.datamodel.Schedule
import myapp.chronify.datamodel.ScheduleUiState
import myapp.chronify.utils.toScheduleEntity

class ScheduleAddViewModel(private val scheduleRepository: ScheduleRepository): ViewModel() {
    /**
     * Holds current schedule ui state
     */
    var scheduleUiState by mutableStateOf(ScheduleUiState())
        private set

    /**
     * Updates the [scheduleUiState] with the value provided in the argument. This method also triggers a validation for input values.
     */
    fun updateUiState(schedule: Schedule) {
        scheduleUiState =
            ScheduleUiState(schedule = schedule, isValid = validateInput(schedule))
    }

    // TODO: to be completed
    private fun validateInput(schedule: Schedule=scheduleUiState.schedule): Boolean {
        return with(schedule) {
            title.isNotBlank()
        }
    }

    /**
     * Inserts a [Schedule] in the Room database
     */
    suspend fun saveScheduleEntity() {
        if (validateInput()) {
            scheduleRepository.insert(scheduleUiState.schedule.toScheduleEntity())
        }
    }
}