package myapp.chronify.data.schedule

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import myapp.chronify.utils.toEpochMillis
import java.time.LocalDate

class ScheduleRepositoryOffline(private val scheduleDao: ScheduleDao) : ScheduleRepository {

    override suspend fun insert(schedule: ScheduleEntity) {
        scheduleDao.insert(schedule)
    }

    override suspend fun update(schedule: ScheduleEntity) {
        scheduleDao.update(schedule)
    }

    override suspend fun delete(schedule: ScheduleEntity) {
        scheduleDao.delete(schedule)
    }

    override fun getScheduleStreamById(id: Int): Flow<ScheduleEntity?> =
        scheduleDao.getScheduleById(id)

    override fun getAllSchedulesStream(): Flow<List<ScheduleEntity>> =
        scheduleDao.getAllSchedules()

    override fun getUnfinishedSchedulesStream(): Flow<List<ScheduleEntity>> =
        scheduleDao.getUnfinishedSchedules()

    override fun getFinishedSchedulesStream(): Flow<List<ScheduleEntity>> =
        scheduleDao.getFinishedSchedules()

    override fun getFinishedSchedulesInRange(from: Long, to: Long): Flow<List<ScheduleEntity>> =
        scheduleDao.getFinishedSchedulesInRange(from, to)

    override fun getSuggestedTitles(query: String): Flow<List<String>> =
        scheduleDao.getSuggestedTitles(query)

    // 获取建议事项
    override fun getSuggestedSchedule(query: String): Flow<List<ScheduleEntity>> =
        scheduleDao.getSuggestedSchedule(query)

    override fun getSchedulesByDate(date: LocalDate): Flow<List<ScheduleEntity>> =
        scheduleDao.getSchedulesByDate(date.toEpochMillis())

    override fun getScheduleStatistics(
        startDate: LocalDate,
        endDate: LocalDate,
        title: String
    ): Flow<Map<LocalDate, Int>> = flow {
        scheduleDao.getScheduleStatistics(
            startDate = startDate.toEpochMillis(),
            endDate = endDate.toEpochMillis(),
            title = title
        ).collect { stringMap ->
            // 将字符串日期转换为LocalDate
            val dateMap = stringMap.mapKeys { (dateStr, _) ->
                LocalDate.parse(dateStr)
            }
            emit(dateMap)
        }
    }
}