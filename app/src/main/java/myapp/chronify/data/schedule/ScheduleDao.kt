package myapp.chronify.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * from ScheduleEntity WHERE id = :id")
    fun getScheduleById(id: Int): Flow<ScheduleEntity>

    @Query("SELECT * from ScheduleEntity ORDER BY createdDT DESC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM ScheduleEntity WHERE isFinished = 0 ORDER BY createdDT DESC")
    fun getUnfinishedSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM ScheduleEntity WHERE isFinished = 1 ORDER BY createdDT DESC")
    fun getFinishedSchedules(): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(schedule: ScheduleEntity)

    @Update
    suspend fun update(schedule: ScheduleEntity)

    @Delete
    suspend fun delete(schedule: ScheduleEntity)


}