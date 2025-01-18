package myapp.chronify.datamodel

import androidx.room.TypeConverter

enum class ScheduleType {
    CYCLICAL,
    REMINDER,
    MISSION,
    CHECK_IN,
    DEFAULT,
}

class Converters {
    @TypeConverter
    fun fromScheduleType(value: ScheduleType): String {
        return value.name
    }

    @TypeConverter
    fun toScheduleType(value: String): ScheduleType {
        return ScheduleType.valueOf(value)
    }
}