package myapp.chronify.data.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ScheduleEntity")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val type: String = "DEFAULT",
    val isFinished: Boolean = false,
    val createdDT: Long = System.currentTimeMillis(),
    val beginDT: Long? = null,
    val endDT: Long? = null,
    // for cyclical schedule
    val interval: Long? = null,
    // extra info
    val location: String? = null,
)
