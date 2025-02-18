package myapp.chronify.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import myapp.chronify.data.PreferencesRepository
import myapp.chronify.data.nife.Nife
import myapp.chronify.data.nife.NifeRepository

data class NifeUiState (
    val nife: Nife = Nife(),
    val isValid: Boolean = false,
    val invalidInfo:String = ""
)

class AddNifeViewModel(
    private val repository: NifeRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    /**
     * Holds current [NifeUiState]
     */
    var uiState by mutableStateOf(NifeUiState())
        private set

    /**
     * Updates the [uiState] with the value provided in the argument. This method also triggers a validation for input values.
     */
    fun updateUiState(nife: Nife) {
        uiState = uiState.copy(
            nife = nife,
            isValid = validateInput(nife)
        )
    }

    /**
     * validates the input values of [Nife] object
     */
    private fun validateInput(nife: Nife=uiState.nife): Boolean {
        // TODO: Add more validation rules, and update invalidInfo
        return with(nife) {
            title.isNotBlank()
        }
    }

    /**
     * Inserts a [Nife] in the Room database
     */
    suspend fun saveNife() {
        if (validateInput()) {
            repository.insert(uiState.nife)
        }
        // TODO: whether to clear the uiState after saving
    }


}


