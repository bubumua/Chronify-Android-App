package myapp.chronify.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiScheduleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(schedule: ShiSchedule)

    @Update
    suspend fun update(schedule: ShiSchedule)

    @Delete
    suspend fun delete(schedule: ShiSchedule)

    @Query("SELECT * from ShiSchedule ORDER BY createdDT DESC")
    fun getAllSchedules(): Flow<List<ShiSchedule>>

    @Query("SELECT * FROM ShiSchedule WHERE isFinished = 0 ORDER BY createdDT DESC")
    fun getUnfinishedSchedules(): Flow<List<ShiSchedule>>

    @Query("SELECT * FROM ShiSchedule WHERE isFinished = 1 ORDER BY createdDT DESC")
    fun getFinishedSchedules(): Flow<List<ShiSchedule>>
}