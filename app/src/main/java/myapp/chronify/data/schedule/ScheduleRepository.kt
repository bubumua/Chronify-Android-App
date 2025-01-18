package myapp.chronify.data.schedule

import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getScheduleStreamById(id: Int): Flow<ScheduleEntity?>
    fun getAllSchedulesStream(): Flow<List<ScheduleEntity>>
    fun getUnfinishedSchedulesStream(): Flow<List<ScheduleEntity>>
    fun getFinishedSchedulesStream(): Flow<List<ScheduleEntity>>
    suspend fun insert(schedule: ScheduleEntity)
    suspend fun update(schedule: ScheduleEntity)
    suspend fun delete(schedule: ScheduleEntity)
}