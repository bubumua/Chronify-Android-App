/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package myapp.chronify.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import myapp.chronify.data.bus.BusSchedule
import myapp.chronify.data.bus.BusScheduleDao
import myapp.chronify.data.schedule.ScheduleDao
import myapp.chronify.data.schedule.ScheduleEntity

@Database(entities = [BusSchedule::class, ScheduleEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun busScheduleDao(): BusScheduleDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "Chronify_database"
                )
                    // .createFromAsset("database/bus_schedule.db")
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
