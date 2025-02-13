import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import myapp.chronify.data.AppDatabase
import myapp.chronify.data.schedule.ScheduleDao
import myapp.chronify.data.schedule.ScheduleEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ScheduleDaoTest {
    private lateinit var scheduleDao: ScheduleDao
    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        scheduleDao = database.scheduleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    private var scheduleEntity1 = ScheduleEntity(
        id = 1,
        title = "Test1",
        description = "Test1",
        beginDT = 0,
        endDT = 0,
        isFinished = false
    )

    private var scheduleEntity2 = ScheduleEntity(
        id = 2,
        title = "Test2",
        description = "Test2",
        beginDT = 0,
        endDT = 0,
        isFinished = true
    )

    private suspend fun insertOneSE() {
        scheduleDao.insert(scheduleEntity1)
    }
    private suspend fun insertTwoSE() {
        scheduleDao.insert(scheduleEntity1)
        scheduleDao.insert(scheduleEntity2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsSE() = runBlocking {
        insertTwoSE()
        val allItems = scheduleDao.getAllSchedules().first()
        val suggestionsTitle = scheduleDao.getSuggestedTitles("es").first()
        Log.d("ScheduleDaoTest", "suggestionsTitle: $suggestionsTitle")
        assertEquals(allItems[0], scheduleEntity1)
        assertEquals(suggestionsTitle[1], scheduleEntity2.title)
    }

}
