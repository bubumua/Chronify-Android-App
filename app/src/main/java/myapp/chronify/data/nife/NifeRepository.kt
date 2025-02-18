package myapp.chronify.data.nife

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class NifeRepository(private val dao: NifeDao) {

    suspend fun insert(nife: Nife) {
        dao.insert(nife)
    }

    suspend fun delete(nife: Nife) {
        dao.delete(nife)
    }

    suspend fun update(nife: Nife) {
        dao.update(nife)
    }

    fun getNifeById(id: Int): Flow<Nife> = dao.getNifeById(id)

    /**
     * 获取所有 Nife 数据
     * @return Flow<List<Nife>>
     * @param pageSize 每页加载数量，默认为 7
     * @param initialLoadSize 首次加载数量，默认为 21
     * @see NifeDao.getAllNifesAsPgSrc
     */
    fun getAllNifesAsPgFlow(
        pageSize: Int = 7,
        initialLoadSize: Int = 21
    ): Flow<PagingData<Nife>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = initialLoadSize
            ),
            pagingSourceFactory = { dao.getAllNifesAsPgSrc() }
        ).flow
    }

    fun getFinishedNifesAsPgFlow(
        pageSize: Int = 7,
        initialLoadSize: Int = 21
    ): Flow<PagingData<Nife>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = initialLoadSize
            ),
            pagingSourceFactory = { dao.getFinishedNifesAsPgSrc() }
        ).flow
    }

    fun getUnfinishedNifesAsPgFlow(
        pageSize: Int = 7,
        initialLoadSize: Int = 21
    ): Flow<PagingData<Nife>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = initialLoadSize
            ),
            pagingSourceFactory = { dao.getUnfinishedNifesAsPgSrc() }
        ).flow
    }

    /**
     * 获取相似标题
     * @return Flow<List<String>>
     * @param query 查询关键字
     * @param limit 返回数量，默认为 6
     * @see NifeDao.getSimilarTitles
     */
    fun getSimilarTitles(query: String, limit: Int = 6): Flow<List<String>> {
        return dao.getSimilarTitles(query, limit)
    }
}