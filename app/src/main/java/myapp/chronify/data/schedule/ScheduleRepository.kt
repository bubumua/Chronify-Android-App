package myapp.chronify.data.schedule

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ScheduleRepository {
    suspend fun insert(schedule: ScheduleEntity)
    suspend fun update(schedule: ScheduleEntity)
    suspend fun delete(schedule: ScheduleEntity)
    fun getScheduleStreamById(id: Int): Flow<ScheduleEntity?>
    fun getAllSchedulesStream(): Flow<List<ScheduleEntity>>
    fun getUnfinishedSchedulesStream(): Flow<List<ScheduleEntity>>
    fun getFinishedSchedulesStream(): Flow<List<ScheduleEntity>>
    fun getFinishedSchedulesInRange(from: Long, to: Long): Flow<List<ScheduleEntity>>
    // 获取标题建议
    fun getSuggestedTitles(query: String): Flow<List<String>>
    // 获取建议事项
    fun getSuggestedSchedule(query: String): Flow<List<ScheduleEntity>>
    // 获取指定日期的所有已完成日程
    fun getSchedulesByDate(date: LocalDate): Flow<List<ScheduleEntity>>
    // 获取指定时间范围内的统计数据
    fun getScheduleStatistics(
        startDate: LocalDate,
        endDate: LocalDate,
        title: String
    ): Flow<Map<LocalDate, Int>>
}