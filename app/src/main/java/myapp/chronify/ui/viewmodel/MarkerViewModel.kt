package myapp.chronify.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import myapp.chronify.data.PreferencesRepository
import myapp.chronify.data.nife.Nife
import myapp.chronify.data.nife.NifeRepository

// 枚举类表示筛选状态
enum class ListFilter {
    UNFINISHED, FINISHED, ALL
}

class MarkerViewModel(
    private val repository: NifeRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    // 添加当前筛选状态
    private val _currentFilter = MutableStateFlow(ListFilter.UNFINISHED)
    val currentFilter = _currentFilter.asStateFlow()

    /**
     * Holds marker screen ui state. The list of nifes are retrieved from [NifeRepository]
     */
    val nifesPagingData: Flow<PagingData<Nife>> =
        combine(
            repository.getUnfinishedNifesAsPgFlow().cachedIn(viewModelScope),
            repository.getFinishedNifesAsPgFlow().cachedIn(viewModelScope),
            repository.getAllNifesAsPgFlow().cachedIn(viewModelScope),
            currentFilter
        ) { unfinished, finished, all, filter ->
            when (filter) {
                ListFilter.UNFINISHED -> unfinished
                ListFilter.FINISHED -> finished
                ListFilter.ALL -> all
            }
        }


    // 添加更新筛选状态的方法
    fun updateFilter(filter: ListFilter) {
        _currentFilter.value = filter
    }

    suspend fun deleteNife(nife: Nife) {
        viewModelScope.launch {
            repository.delete(nife)
        }
            // 等待删除完成,确保删除操作是同步
            .join()

        Log.d("MarkerViewModel", "deleteNife: ${nife.title}")
    }

    suspend fun updateNife(nife: Nife) {
        repository.update(nife)
    }

}