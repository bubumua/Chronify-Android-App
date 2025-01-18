package myapp.chronify.datamodel

data class ScheduleUiState(
    val schedule: Schedule = Schedule(),
    val isValid: Boolean = false
)
