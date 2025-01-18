package myapp.chronify.data

import android.content.Context
import myapp.chronify.data.schedule.ScheduleRepositoryOffline

/**
 * [AppDataContainer] provides instance of OfflineRepository, such as [ScheduleRepositoryOffline].
 */
class AppDataContainer(private val context: Context){
    val scheduleRepositoryOffline: ScheduleRepositoryOffline by lazy {
        ScheduleRepositoryOffline(AppDatabase.getDatabase(context).scheduleDao())
    }

}
