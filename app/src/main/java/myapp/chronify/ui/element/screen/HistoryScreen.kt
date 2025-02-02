package myapp.chronify.ui.element.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import myapp.chronify.R.string
import myapp.chronify.ui.navigation.NavigationDestination
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import myapp.chronify.ui.viewmodel.ScheduleListViewModel

object HistoryScreenDestination : NavigationDestination {
    override val route = "history"
    override val titleRes = string.todo_history
}

@Composable
fun HistoryScreen(
    viewModel: ScheduleListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {}