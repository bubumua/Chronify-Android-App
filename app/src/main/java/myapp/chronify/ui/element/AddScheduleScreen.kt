package myapp.chronify.ui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myapp.chronify.R.string
import myapp.chronify.R.dimen
import myapp.chronify.datamodel.Schedule
import myapp.chronify.datamodel.ScheduleType
import myapp.chronify.datamodel.ScheduleUiState
import myapp.chronify.ui.navigation.NavigationDestination
import myapp.chronify.ui.theme.bluesimple.BlueSimpleTheme
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

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(string.add_schedule_title),
                canNavigateBack = canNavigateBack,
                onBackClick = onNavigateUp
            )
        }
    ) { innerPadding ->
        AddScheduleBody(
            scheduleUiState = viewModel.scheduleUiState,
            onUiStateChange = viewModel::updateUiState,
            onSubmit = {
                coroutineScope.launch {
                    viewModel.saveScheduleEntity()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun AddScheduleBody(
    scheduleUiState: ScheduleUiState,
    onUiStateChange: (Schedule) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(dimen.padding_medium))
    ) {
        ScheduleInputForm(
            schedule = scheduleUiState.schedule,
            onValueChange = onUiStateChange,
            canSubmit = scheduleUiState.isValid,
            onSubmit = onSubmit,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun ScheduleInputForm(
    schedule: Schedule,
    onValueChange: (Schedule) -> Unit,
    onSubmit: () -> Unit,
    canSubmit: Boolean = false,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(dimensionResource(dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(dimen.padding_medium))
    ) {
        // Title
        OutlinedTextField(
            value = schedule.title,
            onValueChange = { onValueChange(schedule.copy(title = it)) },
            label = { Text(stringResource(string.title_req)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            singleLine = true
        )

        // Type and isFinished
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnumDropdown(
                label = stringResource(string.title_req),
                initialValue = ScheduleType.DEFAULT,
                onValueSelected = {
                    onValueChange(
                        schedule.copy(
                            type = it,
                            isFinished = when (it) {
                                ScheduleType.CHECK_IN -> true
                                else -> false
                            }
                        )
                    )
                },
                modifier = Modifier.weight(1f, true)
            )
            Spacer(modifier = Modifier.width(dimensionResource(dimen.padding_medium)))
            Switch(
                checked = schedule.isFinished,
                onCheckedChange = { onValueChange(schedule.copy(isFinished = it)) }
            )
        }
        // TODO: beginDT and endDT

        // TODO: interval

        // description
        OutlinedTextField(
            value = schedule.description ?: "",
            onValueChange = { onValueChange(schedule.copy(description = it)) },
            label = { Text(stringResource(string.description_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        // TODO: location
        OutlinedTextField(
            value = schedule.location ?: "",
            onValueChange = { onValueChange(schedule.copy(location = it)) },
            label = { Text(stringResource(string.location_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        // submit button
        Button(
            onClick = onSubmit,
            enabled = canSubmit,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(string.submit))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddScheduleScreenPreview() {
    BlueSimpleTheme {
        ScheduleInputForm(Schedule(), {}, {})
    }

}