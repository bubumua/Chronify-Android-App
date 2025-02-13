package myapp.chronify.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(schedule: ScheduleEntity)

    @Update
    suspend fun update(schedule: ScheduleEntity)

    @Delete
    suspend fun delete(schedule: ScheduleEntity)

    @Query("SELECT * from ScheduleEntity WHERE id = :id")
    fun getScheduleById(id: Int): Flow<ScheduleEntity>

    @Query("SELECT * from ScheduleEntity ORDER BY createdDT DESC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM ScheduleEntity WHERE isFinished = 0 ORDER BY createdDT DESC")
    fun getUnfinishedSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM ScheduleEntity WHERE isFinished = 1 ORDER BY createdDT DESC")
    fun getFinishedSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM ScheduleEntity WHERE isFinished = 1 AND endDT BETWEEN :startDate AND :endDate ORDER BY endDT")
    fun getFinishedSchedulesInRange(startDate: Long, endDate: Long): Flow<List<ScheduleEntity>>

    @Query(
        """
        SELECT DISTINCT title 
        FROM ScheduleEntity 
        WHERE title LIKE '%' || :query || '%' 
        LIMIT 10
    """
    )
    fun getSuggestedTitles(query: String): Flow<List<String>>

    @Query(
        """
        SELECT *
        FROM ScheduleEntity 
        WHERE title LIKE '%' || :query || '%' 
        LIMIT 10
    """
    )
    fun getSuggestedSchedule(query: String): Flow<List<ScheduleEntity>>

    @Query(
        """
        SELECT * 
        FROM ScheduleEntity 
        WHERE isFinished = 1 
        AND date(endDT/1000, 'unixepoch') = date(:date/1000, 'unixepoch')
        ORDER BY endDT DESC
    """
    )
    fun getSchedulesByDate(date: Long): Flow<List<ScheduleEntity>>

    @MapInfo(keyColumn = "date", valueColumn = "count")
    @Query(
        """
        SELECT date(endDT/1000, 'unixepoch') as date, COUNT(*) as count
        FROM ScheduleEntity
        WHERE isFinished = 1
        AND endDT BETWEEN :startDate AND :endDate
        AND (:title = '' OR title LIKE '%' || :title || '%')
        GROUP BY date(endDT/1000, 'unixepoch')
    """
    )
    fun getScheduleStatistics(
        startDate: Long,
        endDate: Long,
        title: String
    ): Flow<Map<String, Int>>
}