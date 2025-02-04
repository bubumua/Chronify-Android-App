package myapp.chronify.ui.element.screen

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myapp.chronify.R.string
import myapp.chronify.ui.element.AppTopBar
import myapp.chronify.ui.navigation.NavigationDestination
import myapp.chronify.ui.viewmodel.ScheduleAddViewModel
import myapp.chronify.ui.viewmodel.AppViewModelProvider

object AddScheduleScreenDestination : NavigationDestination {
    override val route = "add"
    override val titleRes = string.add_schedule_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAddScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ScheduleAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Scaffold(
    //     topBar = {
    //         AppTopBar(
    //             title = stringResource(string.add_schedule_title),
    //             // canNavigateBack = canNavigateBack,
    //             // onBackClick = onNavigateUp
    //         )
    //     }
    // ) { innerPadding ->
    //     EditScreenBody(
    //         scheduleUiState = viewModel.scheduleUiState,
    //         onUiStateChange = viewModel::updateUiState,
    //         onSubmit = {
    //             coroutineScope.launch {
    //                 viewModel.saveScheduleEntity()
    //                 navigateBack()
    //             }
    //         },
    //         modifier = Modifier
    //             .padding(
    //                 start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
    //                 end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
    //                 top = innerPadding.calculateTopPadding()
    //             )
    //             .verticalScroll(rememberScrollState())
    //             .fillMaxWidth()
    //     )
    // }
}
