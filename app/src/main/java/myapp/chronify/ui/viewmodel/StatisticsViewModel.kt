package myapp.chronify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import myapp.chronify.data.PreferencesRepository
import myapp.chronify.data.nife.MonthCount
import myapp.chronify.data.nife.Nife
import myapp.chronify.data.nife.NifeRepository
import java.time.LocalDate

class StatisticsViewModel(
    private val repository: NifeRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    enum class TimeRange {
        WEEK, MONTH, YEAR
    }

    // UI States
    data class StatisticsUiState(
        val searchQuery: String = "",
        val suggestions: List<String> = emptyList(),
        val dateEventMap: Map<LocalDate, List<Nife>> = emptyMap(),
        val timeRange: TimeRange = TimeRange.MONTH,
        val monthCount: List<MonthCount> = emptyList(),
        val isCalendarView: Boolean = true,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 动态响应的 PagingData
    @OptIn(ExperimentalCoroutinesApi::class)
    val nifesPagingData: Flow<PagingData<Nife>> = searchQuery
        .flatMapLatest { query ->
            repository.getFinishedNifesByTitleAsPgFlow(query)
        }
        .cachedIn(viewModelScope)

    // 处理搜索框输入
    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query) }
            _searchQuery.value = query // 更新 StateFlow
            if (query.isNotBlank()) {
                // 获取标题建议
                val suggestions = repository.getSimilarTitles(query).first()
                // 获取月份统计
                val monthCount = repository.getMonthCount(query).first()
                    .map { it.copy(month = it.month.substring(2, 7)) }
                _uiState.update {
                    it.copy(
                        suggestions = suggestions,
                        monthCount = monthCount
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        suggestions = emptyList(),
                        monthCount = emptyList()
                    )
                }
            }
            // update month count

        }
    }

    // 切换视图模式
    fun toggleViewMode() {
        _uiState.update { it.copy(isCalendarView = !it.isCalendarView) }
    }

    fun convertToDateEventMap(items: List<Nife>): Map<LocalDate, List<Nife>> {
        return items
            .mapNotNull { nife ->
                nife.endDT?.toLocalDate()?.let { date -> date to nife }
            }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
    }
}
