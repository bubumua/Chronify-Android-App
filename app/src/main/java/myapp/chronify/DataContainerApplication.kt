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
package myapp.chronify

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import myapp.chronify.data.AppDataContainer
import myapp.chronify.data.AppDatabase
import myapp.chronify.data.PreferencesRepository

class DataContainerApplication: Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppDataContainer

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    lateinit var preferencesRepository: PreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        preferencesRepository = PreferencesRepository(this)

        // 在应用启动时初始化偏好设置
        // CoroutineScope(Dispatchers.IO).launch {
        //     Log.d("DataContainerApplication", "Initializing preferences")
        //     preferencesRepository.initializePreferences()
        // }
    }
}
