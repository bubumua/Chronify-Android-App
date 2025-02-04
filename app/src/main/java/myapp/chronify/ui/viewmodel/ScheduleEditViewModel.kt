package myapp.chronify.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import myapp.chronify.data.schedule.ScheduleRepository
import myapp.chronify.datamodel.Schedule
import myapp.chronify.ui.element.screen.EditScheduleScreenDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import myapp.chronify.datamodel.ScheduleUiState
import myapp.chronify.utils.toScheduleEntity
import myapp.chronify.utils.toScheduleUiState


/**
 * ViewModel to retrieve, update an schedule from the [ScheduleRepository]'s data source.
 */
class ScheduleEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {

    private val scheduleId: Int =
        checkNotNull(savedStateHandle[EditScheduleScreenDestination.itemIdArg])

    /**
     * Holds current edit schedule ui state
     */
    var uiState by mutableStateOf(ScheduleUiState())
        private set

    init {
        viewModelScope.launch {
            uiState =
                scheduleRepository.getScheduleStreamById(scheduleId)
                    .filterNotNull()
                    .first()
                    .toScheduleUiState(true)
        }
    }

    private fun validateInput(schedule: Schedule = uiState.schedule): Boolean {
        return with(schedule) {
            title.isNotBlank()
        }
    }

    /**
     * Updates the [uiState] with the value provided in the argument. This method also triggers a validation for input values.
     */
    fun updateUiState(schedule:Schedule) {
        uiState = uiState.copy(
            schedule = schedule,
            isValid = validateInput(schedule)
        )
    }

    private fun modifyScheduleUiState() {
    }

    /**
     * Update the [Schedule] in the Room database
     */
    suspend fun updateScheduleEntity() {
        if (validateInput()) {
            modifyScheduleUiState()
            scheduleRepository.update(uiState.schedule.toScheduleEntity())
        }
    }
}