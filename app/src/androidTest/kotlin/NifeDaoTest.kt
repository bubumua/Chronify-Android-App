import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import myapp.chronify.data.AppDatabase
import myapp.chronify.data.nife.Nife
import myapp.chronify.data.nife.NifeDao
import myapp.chronify.data.nife.NifeType
import myapp.chronify.data.nife.PeriodType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class NifeDaoTest {
    private lateinit var nifeDao: NifeDao
    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the process is killed.
        database = Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        nifeDao = database.nifeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    private var nife1 = Nife(
        id = 1,
        title = "Test1",
        type = NifeType.CYCLICAL,
        createdDT = LocalDateTime.now(),
        period = PeriodType.WEEKLY,
        periodMultiple = 2,
        triggerTimes = setOf(1, 3, 5),
    )

    private var nife2 = Nife(
        id = 2,
        title = "Test2",
        type = NifeType.RECORD,
        createdDT = LocalDateTime.now(),
        period = PeriodType.DAILY,
        periodMultiple = 2,
        triggerTimes = setOf(1, 3, 5),
    )

    private suspend fun insertOne() {
        nifeDao.insert(nife1)
    }
    private suspend fun insertTwo() {
        nifeDao.insert(nife1)
        nifeDao.insert(nife2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsSE() = runBlocking {
        insertTwo()
        val allItems = nifeDao.getAllNifes().first()
        assertEquals(allItems[0], nife2)
        // assertEquals(suggestionsTitle[1], scheduleEntity2.title)
    }
}