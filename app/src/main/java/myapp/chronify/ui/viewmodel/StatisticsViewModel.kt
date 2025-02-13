package myapp.chronify.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import myapp.chronify.data.PreferencesRepository
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.data.schedule.ScheduleRepository
import myapp.chronify.datamodel.Schedule
import myapp.chronify.utils.daysUntil
import myapp.chronify.utils.toSchedule
import java.time.LocalDate

class StatisticsViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    enum class TimeRange {
        WEEK, MONTH, YEAR
    }

    // UI States
    data class StatisticsUiState(
        val searchQuery: String = "",
        val suggestions: List<String> = emptyList(),
        val isCalendarView: Boolean = true,
        val selectedDate: LocalDate? = null,
        val selectedDateSchedules: List<Schedule> = emptyList(),
        val timeRange: TimeRange = TimeRange.MONTH,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class DayStatistics(
        val date: LocalDate,
        val count: Int,
        val isHighlighted: Boolean = false
    )

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    // 存储当前可见的日历数据
    private val _calendarData = MutableStateFlow<List<DayStatistics>>(emptyList())
    val calendarData: StateFlow<List<DayStatistics>> = _calendarData.asStateFlow()

    // 存储图表数据
    private val _chartData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val chartData: StateFlow<Map<LocalDate, Int>> = _chartData.asStateFlow()

    data class TestListUiState(val scheduleList: List<ScheduleEntity> = listOf())

    // 处理搜索框输入
    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query) }
            // 获取标题建议
            if (query.isNotBlank()) {
                val suggestions = scheduleRepository.getSuggestedTitles(query).first()
                _uiState.update { it.copy(suggestions = suggestions) }
            } else {
                _uiState.update { it.copy(suggestions = emptyList()) }
            }
            // 更新统计数据
            updateStatistics()
        }
    }

    // 切换视图模式
    fun toggleViewMode() {
        _uiState.update { it.copy(isCalendarView = !it.isCalendarView) }
        updateStatistics()
    }

    // 选择日期
    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            val schedules = scheduleRepository.getSchedulesByDate(date).first()
                .map {
                    schedule -> schedule.toSchedule()
                }
            _uiState.update {
                it.copy(
                    selectedDate = date,
                    selectedDateSchedules = schedules
                )
            }
        }
    }

    // 更改时间范围（周/月/年）
    fun setTimeRange(timeRange: TimeRange) {
        _uiState.update { it.copy(timeRange = timeRange) }
        updateStatistics()
    }

    // 更新统计数据
    private fun updateStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (_uiState.value.isCalendarView) {
                    updateCalendarData()
                } else {
                    updateChartData()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "An error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }

    // 更新日历数据，默认显示最近3个月的数据
    private suspend fun updateCalendarData(previousCount: Long = 3) {
        // 计算需要显示的时间范围
        val endDate = LocalDate.now()
        val startDate = endDate.minusMonths(previousCount)

        // 分页获取数据
        val statistics = scheduleRepository.getScheduleStatistics(
            startDate = startDate,
            endDate = endDate,
            title = _uiState.value.searchQuery
        ).first()

        // 转换为UI数据模型
        val dayStatistics = (0..startDate.daysUntil(endDate)).map { days ->
            val date = startDate.plusDays(days)
            val count = statistics[date] ?: 0
            val isHighlighted = _uiState.value.searchQuery.isNotBlank() && count > 0
            DayStatistics(date, count, isHighlighted)
        }

        _calendarData.value = dayStatistics
        _uiState.update { it.copy(isLoading = false) }
    }

    // 更新图表数据
    private suspend fun updateChartData() {
        val endDate = LocalDate.now()
        val startDate = when (_uiState.value.timeRange) {
            TimeRange.WEEK -> endDate.minusWeeks(1)
            TimeRange.MONTH -> endDate.minusMonths(1)
            TimeRange.YEAR -> endDate.minusYears(1)
        }

        val statistics = scheduleRepository.getScheduleStatistics(
            startDate = startDate,
            endDate = endDate,
            title = _uiState.value.searchQuery
        ).first()

        _chartData.value = statistics
        _uiState.update { it.copy(isLoading = false) }
    }

    // 加载更多历史数据（用于日历视图的滚动加载）
    fun loadMoreHistory() {
        viewModelScope.launch {
            val currentOldestDate = _calendarData.value.firstOrNull()?.date ?: return@launch
            val newStartDate = currentOldestDate.minusMonths(3)

            val additionalStatistics = scheduleRepository.getScheduleStatistics(
                startDate = newStartDate,
                endDate = currentOldestDate.minusDays(1),
                title = _uiState.value.searchQuery
            ).first()

            // 将新数据添加到现有数据的前面
            val newDayStatistics = (0..newStartDate.daysUntil(currentOldestDate)).map { days ->
                val date = newStartDate.plusDays(days)
                val count = additionalStatistics[date] ?: 0
                val isHighlighted = _uiState.value.searchQuery.isNotBlank() && count > 0
                DayStatistics(date, count, isHighlighted)
            }

            _calendarData.value = newDayStatistics + _calendarData.value
        }
    }
}