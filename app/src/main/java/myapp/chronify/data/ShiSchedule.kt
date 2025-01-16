package myapp.chronify.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ShiSchedule")
data class ShiSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val title: String,
    val type: String,
    val isFinished: Boolean,
    val createdDT: Long = System.currentTimeMillis(),
    val beginDT: Long? = null,
    val endDT: Long? = null,
    // for cyclical schedule
    val interval: Long? = null,
    // extra info
    val location: String? = null,
)
