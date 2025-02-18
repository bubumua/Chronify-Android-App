package myapp.chronify.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import myapp.chronify.DataContainerApplication


const val TIMEOUT_MILLIS = 5_000L

/**
 * Extension function to queries for [Application] object and returns an instance of [DataContainerApplication].
 */
fun CreationExtras.DataContainerApplication(): DataContainerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DataContainerApplication)


/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            MarkerViewModel(
                DataContainerApplication().container.nifeRepository,
                DataContainerApplication().preferencesRepository
            )
        }
        initializer {
            NifeAddViewModel(
                DataContainerApplication().container.nifeRepository,
                DataContainerApplication().preferencesRepository
            )
        }
        initializer {
            NifeEditViewModel(
                this.createSavedStateHandle(),
                DataContainerApplication().container.nifeRepository,
                DataContainerApplication().preferencesRepository
            )
        }
        initializer {
            StatisticsViewModel(
                DataContainerApplication().container.nifeRepository,
                DataContainerApplication().preferencesRepository
            )
        }
        initializer {
            SettingsViewModel(
                DataContainerApplication().container.nifeRepository,
                DataContainerApplication().preferencesRepository
            )
        }

    }
}

