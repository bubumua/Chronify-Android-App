package myapp.chronify.ui.element.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import myapp.chronify.ui.navigation.NavigationRoute
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.launch
import myapp.chronify.R.string
import myapp.chronify.data.PreferencesKey
import myapp.chronify.ui.element.AppTopBar
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import myapp.chronify.ui.viewmodel.SettingsUiState
import myapp.chronify.ui.viewmodel.SettingsViewModel

object SettingsScreenRoute : NavigationRoute {
    override val route = "settings"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = stringResource(string.setting),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            SettingsContent(viewModel = viewModel)
        }
    }
}

@Composable
fun SettingsContent(
    viewModel: SettingsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val settingsMap by viewModel.settingsMap.collectAsState()

    // TODO: Add more settings
    Column {
        Switch(
            checked = settingsMap[PreferencesKey.DisplayPref.WeekStartFromSunday] as Boolean,
            onCheckedChange = {
                coroutineScope.launch {
                    viewModel.updatePreference(PreferencesKey.DisplayPref.WeekStartFromSunday, it)
                }
            },
        )
        // Text(settings.theme)

    }
}
