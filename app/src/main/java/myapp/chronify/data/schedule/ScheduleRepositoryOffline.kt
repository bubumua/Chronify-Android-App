package myapp.chronify.data.schedule

import kotlinx.coroutines.flow.Flow

class ScheduleRepositoryOffline(private val scheduleDao: ScheduleDao) : ScheduleRepository {

    override fun getScheduleStreamById(id: Int): Flow<ScheduleEntity?> =
        scheduleDao.getScheduleById(id)

    override fun getAllSchedulesStream(): Flow<List<ScheduleEntity>> =
        scheduleDao.getAllSchedules()

    override fun getUnfinishedSchedulesStream(): Flow<List<ScheduleEntity>> =
        scheduleDao.getUnfinishedSchedules()

    override fun getFinishedSchedulesStream(): Flow<List<ScheduleEntity>> =
        scheduleDao.getFinishedSchedules()

    override suspend fun insert(schedule: ScheduleEntity) {
        scheduleDao.insert(schedule)
    }

    override suspend fun update(schedule: ScheduleEntity) {
        scheduleDao.update(schedule)
    }

    override suspend fun delete(schedule: ScheduleEntity) {
        scheduleDao.delete(schedule)
    }
}