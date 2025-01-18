package myapp.chronify.datamodel

data class Schedule(
    val id: Int = 0,
    val title: String = "default schedule",
    val type: ScheduleType = ScheduleType.DEFAULT,
    val isFinished: Boolean = false,
    val createdDT: Long = System.currentTimeMillis(),
    val beginDT: Long? = null,
    val endDT: Long? = null,
    // for cyclical schedule
    val interval: Long? = null,
    // extra info
    val location: String? = null,
)
