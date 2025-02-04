package myapp.chronify.utils

import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.datamodel.Schedule
import myapp.chronify.datamodel.ScheduleType
import myapp.chronify.datamodel.ScheduleUiState

/**
 * Extension function to convert [Schedule] to [ScheduleEntity].
 */
fun Schedule.toScheduleEntity(): ScheduleEntity = ScheduleEntity(
    id = id,
    title = title,
    type = type.name,
    isFinished = isFinished,
    createdDT = createdDT,
    beginDT = beginDT,
    endDT = endDT,
    interval = interval,
    description = description,
    location = location,
)

/**
 * Extension function to convert [ScheduleEntity] to [Schedule].
 */
fun ScheduleEntity.toSchedule(): Schedule = Schedule(
    id = id,
    title = title,
    type = ScheduleType.valueOf(type),
    isFinished = isFinished,
    createdDT = createdDT,
    beginDT = beginDT,
    endDT = endDT,
    interval = interval,
    description = description,
    location = location,
)

/**
 * Extension function to convert [ScheduleEntity] to [ScheduleUiState].
 */
fun ScheduleEntity.toScheduleUiState(isValid: Boolean=false): ScheduleUiState = ScheduleUiState(
    schedule = this.toSchedule(),
    isValid = isValid
)
