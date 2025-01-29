package myapp.chronify.datamodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import myapp.chronify.R.string
import myapp.chronify.R.drawable
import androidx.room.TypeConverter

enum class ScheduleType {
    REMINDER,
    CHECK_IN,
    CYCLICAL,
    MISSION,
    DEFAULT,
}

@Composable
fun ScheduleType.getLocalizedName(): String {
    return when (this) {
        ScheduleType.REMINDER -> stringResource(string.schedule_type_reminder)
        ScheduleType.DEFAULT -> stringResource(string.schedule_type_default)
        ScheduleType.CHECK_IN -> stringResource(string.schedule_type_check_in)
        ScheduleType.CYCLICAL -> stringResource(string.schedule_type_cyclical)
        ScheduleType.MISSION -> stringResource(string.schedule_type_mission)
        else -> stringResource(string.schedule_type_undefined)
    }
}

@Composable
fun ScheduleType.getIcon(): Int {
    return when (this) {
        ScheduleType.REMINDER -> drawable.event_note_24px
        ScheduleType.DEFAULT -> drawable.calendar_add_on_24px
        ScheduleType.CHECK_IN -> drawable.event_available_24px
        ScheduleType.CYCLICAL -> drawable.event_repeat_24dp_e8eaed_fill0_wght400_grad0_opsz24
        ScheduleType.MISSION -> drawable.event_note_24px
        else -> drawable.event_note_24px
    }
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