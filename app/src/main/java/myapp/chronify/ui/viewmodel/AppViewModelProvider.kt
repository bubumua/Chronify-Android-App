package myapp.chronify.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import myapp.chronify.DataContainerApplication

/**
 * Extension function to queries for [Application] object and returns an instance of [DataContainerApplication].
 */
fun CreationExtras.DataContainerApplication(): DataContainerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DataContainerApplication)


/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory : ViewModelProvider.Factory = viewModelFactory{
        initializer{
            ScheduleAddViewModel(DataContainerApplication().container.scheduleRepositoryOffline)
        }
        initializer {
            RemindViewModel(DataContainerApplication().container.scheduleRepositoryOffline)
        }
    }
}

