package myapp.chronify.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import myapp.chronify.R.string
import java.time.temporal.ChronoUnit

// 转换为epochMillis（从1970-01-01开始的毫秒数）
fun LocalDate.toEpochMillis(): Long {
    return this.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun LocalDate.daysUntil(endDate: LocalDate): Long {
    return ChronoUnit.DAYS.between(this, endDate)
}

// Long转回LocalDateTime
fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

// 分别获取日期和时间
fun Long.toLocalDate(): LocalDate = this.toLocalDateTime().toLocalDate()
fun Long.toLocalTime(): LocalTime = this.toLocalDateTime().toLocalTime()

// 从TimePickerState获取LocalTime
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.toLocalTime(): LocalTime {
    return LocalTime.of(this.hour, this.minute)
}

// 组合LocalDate和TimePickerState为Long
@OptIn(ExperimentalMaterial3Api::class)
fun combineDateTimeState(date: LocalDate, timeState: TimePickerState): Long {
    val time = timeState.toLocalTime()
    return LocalDateTime.of(date, time)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

// Long转换为hour和minute (用于设置TimePickerState的初始值)
fun Long.toHourMinute(): Pair<Int, Int> {
    val localTime = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    return Pair(localTime.hour, localTime.minute)
}

// 组合日期和时间，转换为Long
fun combineLocalDateLocalTime(date: LocalDate, time: LocalTime): Long {
    return LocalDateTime.of(date, time)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

@Composable
fun LocalDateTime.toFriendlyString(): String {
    val now = LocalDateTime.now()

    return when {
        this.toLocalDate() == now.minusDays(2).toLocalDate() ->
            "${stringResource(string.ereyesterday)} ${this.toLocalTime()}"

        this.toLocalDate() == now.minusDays(1).toLocalDate() ->
            "${stringResource(string.yesterday)} ${this.toLocalTime()}"

        this.toLocalDate() == now.toLocalDate() ->
            "${stringResource(string.today)} ${this.toLocalTime()}"

        this.toLocalDate() == now.plusDays(1).toLocalDate() ->
            "${stringResource(string.tomorrow)} ${this.toLocalTime()}"

        this.toLocalDate() == now.plusDays(2).toLocalDate() ->
            "${stringResource(string.overmorrow)} ${this.toLocalTime()}"

        this.year == now.year ->
            this.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))

        else ->
            this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}

object MyDateTimeFormatter {
    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val isoOnSecFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val isoOnMinFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeOnlyFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    // 转换为ISO格式
    fun Long.toISOString(): String {
        return this.toLocalDateTime().format(isoFormatter)
    }

    // 转换为精度为秒的ISO自定义格式
    fun Long.toIsoOnSecString(): String {
        return this.toLocalDateTime().format(isoOnSecFormatter)
    }

    // 转换为精度为分的ISO自定义格式
    fun Long.toIsoOnMinString(): String {
        return this.toLocalDateTime().format(isoOnMinFormatter)
    }

    // 只显示日期
    fun Long.toDateString(): String {
        return this.toLocalDateTime().format(dateOnlyFormatter)
    }

    // 只显示时间
    fun Long.toTimeString(): String {
        return this.toLocalDateTime().format(timeOnlyFormatter)
    }

    // 转换为友好格式（例如："今天 14:30"，"昨天 15:20"，"2023年12月1日 16:40"）
    @Composable
    fun Long.toFriendlyString(): String {
        val dateTime = this.toLocalDateTime()
        val now = LocalDateTime.now()

        return when {
            dateTime.toLocalDate() == now.toLocalDate() ->
                "${dateTime.format(timeOnlyFormatter)}"

            dateTime.toLocalDate() == now.minusDays(1).toLocalDate() ->
                "${stringResource(string.yesterday)} ${dateTime.format(timeOnlyFormatter)}"

            dateTime.year == now.year ->
                dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))

            else ->
                dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd- HH:mm"))
        }
    }

}