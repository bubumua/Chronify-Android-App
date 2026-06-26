package myapp.chronify.ui.element.screen


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myapp.chronify.R.dimen
import myapp.chronify.R.string
import myapp.chronify.data.PreferencesKey
import myapp.chronify.ui.element.components.AppTopBar
import myapp.chronify.ui.navigation.NavigationRoute
import myapp.chronify.ui.viewmodel.AppViewModelProvider
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
    val context = LocalContext.current
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importFromUri(context, it) }
    }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { viewModel.exportToDirectory(context, it) }
    }

    Column {
        // Display settings
        Text(
            text = stringResource(string.display_settings),
            style = MaterialTheme.typography.titleLarge
        )
        SettingsItemView(title = stringResource(string.week_start_from_sunday)) {
            Switch(
                checked = settingsMap[PreferencesKey.DisplayPref.WeekStartFromSunday] as Boolean,
                onCheckedChange = {
                    coroutineScope.launch {
                        viewModel.updatePreference(
                            PreferencesKey.DisplayPref.WeekStartFromSunday,
                            it
                        )
                    }
                },
            )
        }

        // Import/Export settings
        Text(
            text = stringResource(string.import_export_settings),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(dimen.padding_medium))
        ) {
            Button(onClick = {
                importLauncher.launch(arrayOf("text/*", "text/comma-separated-values"))
            }) {
                Text(stringResource(string.import_))
            }
            Button(onClick = {
                exportLauncher.launch(null)
            }) {
                Text(stringResource(string.export))
            }
        }
    }
}

@Composable
private fun SettingsItemView(
    title: String,
    description: String = "",
    settingController: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(dimen.padding_medium))
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (description.isNotBlank())
                Text(text = description, style = MaterialTheme.typography.titleSmall)
        }
        settingController()
    }
}


