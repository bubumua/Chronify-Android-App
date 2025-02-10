package myapp.chronify.ui.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.data.schedule.ScheduleRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DaySchedules(
    val date: LocalDate,
    val schedules: List<ScheduleWithColor>
)

data class ScheduleWithColor(
    val schedule: ScheduleEntity,
    val color: Color
)

class ScheduleCalendarViewModel (
    private val repository: ScheduleRepository
) : ViewModel() {

    // 存储 title -> Color 的映射
    private val titleColorMap = mutableMapOf<String, Color>()

    // 可用的颜色列表
    private val availableColors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFFE91E63), // Pink
        Color(0xFF2196F3), // Blue
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF009688), // Teal
        // 可以添加更多颜色
    )

    // private val _calendarData = MutableStateFlow<List<DaySchedules>>(emptyList())
    // val calendarData: StateFlow<List<DaySchedules>> = _calendarData.asStateFlow()
    //
    // private val _visibleDateRange = MutableStateFlow<Pair<LocalDate, LocalDate>?>(null)
    // val visibleDateRange: StateFlow<Pair<LocalDate, LocalDate>?> = _visibleDateRange.asStateFlow()
    //
    // private val _totalDateRange = MutableStateFlow<Pair<LocalDate, LocalDate>?>(null)
    // val totalDateRange: StateFlow<Pair<LocalDate, LocalDate>?> = _totalDateRange.asStateFlow()
    //
    // // 缓存每天的详细数据
    // private val dayDetailsCache = mutableMapOf<LocalDate, List<ScheduleWithColor>>()
    //
    // init {
    //     viewModelScope.launch {
    //         repository.getFinishedSchedulesStream().collect { schedules ->
    //             processSchedules(schedules)
    //         }
    //     }
    // }
    // init {
    //     viewModelScope.launch {
    //         fetchTotalDateRange()
    //     }
    // }
    //
    // private suspend fun fetchTotalDateRange() {
    //     try {
    //         val range = repository.getScheduleDateRange()
    //         range?.let { (startMillis, endMillis) ->
    //             val startDate = Instant.ofEpochMilli(startMillis)
    //                 .atZone(ZoneId.systemDefault())
    //                 .toLocalDate()
    //             val endDate = Instant.ofEpochMilli(endMillis)
    //                 .atZone(ZoneId.systemDefault())
    //                 .toLocalDate()
    //             _totalDateRange.value = Pair(startDate, endDate)
    //         }
    //     } catch (e: Exception) {
    //         // 处理错误，可以添加错误状态或日志
    //         Log.e("ScheduleCalendarViewModel", "Error fetching date range", e)
    //     }
    // }
    //
    // fun loadDateRange(startDate: LocalDate, endDate: LocalDate) {
    //     viewModelScope.launch {
    //         try {
    //             _visibleDateRange.value = Pair(startDate, endDate)
    //
    //             // 转换日期为毫秒时间戳
    //             val startMillis = startDate.atStartOfDay(ZoneId.systemDefault())
    //                 .toInstant()
    //                 .toEpochMilli()
    //             val endMillis = endDate.plusDays(1)
    //                 .atStartOfDay(ZoneId.systemDefault())
    //                 .minusSeconds(1)
    //                 .toInstant()
    //                 .toEpochMilli()
    //
    //             // 获取指定日期范围内的数据
    //             repository.getFinishedSchedulesInRange(startMillis, endMillis)
    //                 .collect { schedules ->
    //                     processSchedules(schedules)
    //                 }
    //         } catch (e: Exception) {
    //             Log.e("ScheduleCalendarViewModel", "Error loading date range", e)
    //         }
    //     }
    // }
    //
    // private fun processSchedulesOld(schedules: List<ScheduleEntity>) {
    //     // 为每个独特的 title 分配颜色
    //     schedules.forEach { schedule ->
    //         if (!titleColorMap.containsKey(schedule.title)) {
    //             val unusedColor = availableColors[titleColorMap.size % availableColors.size]
    //             titleColorMap[schedule.title] = unusedColor
    //         }
    //     }
    //
    //     // 按日期分组并处理数据
    //     val schedulesWithColor = schedules.map { schedule ->
    //         ScheduleWithColor(
    //             schedule = schedule,
    //             color = titleColorMap[schedule.title] ?: availableColors[0]
    //         )
    //     }
    //
    //     val groupedByDate = schedulesWithColor
    //         .groupBy { schedule ->
    //             schedule.schedule.endDT!!.let {
    //                 Instant.ofEpochMilli(it)
    //                     .atZone(ZoneId.systemDefault())
    //                     .toLocalDate()
    //             }
    //         }
    //         .map { (date, schedules) ->
    //                 DaySchedules(date, schedules)
    //         }
    //         .sortedBy { it.date }
    //
    //     _calendarData.value = groupedByDate
    // }
    //
    // private fun processSchedules(schedules: List<ScheduleEntity>) {
    //     // 为新的 title 分配颜色
    //     schedules.forEach { schedule ->
    //         if (!titleColorMap.containsKey(schedule.title)) {
    //             val unusedColor = availableColors[titleColorMap.size % availableColors.size]
    //             titleColorMap[schedule.title] = unusedColor
    //         }
    //     }
    //
    //     // 转换为带颜色的日程数据
    //     val schedulesWithColor = schedules.map { schedule ->
    //         ScheduleWithColor(
    //             schedule = schedule,
    //             color = titleColorMap[schedule.title] ?: availableColors[0]
    //         )
    //     }
    //
    //     // 按日期分组
    //     val groupedByDate = schedulesWithColor
    //         .groupBy { schedule ->
    //             Instant.ofEpochMilli(schedule.schedule.endDT ?: 0L)
    //                 .atZone(ZoneId.systemDefault())
    //                 .toLocalDate()
    //         }
    //         .map { (date, schedules) ->
    //             DaySchedules(date, schedules)
    //         }
    //         .sortedBy { it.date }
    //
    //     // 更新状态和缓存
    //     _calendarData.value = groupedByDate
    //     // 更新详情缓存
    //     groupedByDate.forEach { daySchedules ->
    //         dayDetailsCache[daySchedules.date] = daySchedules.schedules
    //     }
    // }
    //
    // // 获取日期范围
    // fun getDateRange(): Pair<LocalDate, LocalDate>? {
    //     val schedules = _calendarData.value
    //     if (schedules.isEmpty()) return null
    //
    //     return Pair(
    //         schedules.first().date,
    //         schedules.last().date
    //     )
    // }
    //
    // fun getDayDetails(date: LocalDate): List<ScheduleWithColor> {
    //     // 首先尝试从缓存获取
    //     return dayDetailsCache[date] ?: run {
    //         // 如果缓存中没有，从当前数据中查找
    //         _calendarData.value
    //             .find { it.date == date }
    //             ?.schedules
    //             ?: emptyList()
    //     }
    // }
    //
    // // 清理函数
    // override fun onCleared() {
    //     super.onCleared()
    //     dayDetailsCache.clear()
    //     titleColorMap.clear()
    // }
}
