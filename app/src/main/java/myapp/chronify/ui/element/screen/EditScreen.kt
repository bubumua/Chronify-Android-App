package myapp.chronify.ui.element.screen

import android.util.Log
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myapp.chronify.ui.navigation.NavigationDestination
import myapp.chronify.R.string
import myapp.chronify.R.dimen
import myapp.chronify.datamodel.Schedule
import myapp.chronify.datamodel.ScheduleType
import myapp.chronify.datamodel.ScheduleUiState
import myapp.chronify.ui.element.AppTopBar
import myapp.chronify.ui.element.DateTimePickerDialog
import myapp.chronify.ui.element.EnumDropdown
import myapp.chronify.ui.theme.bluesimple.BlueSimpleTheme
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import myapp.chronify.ui.viewmodel.ScheduleEditViewModel
import myapp.chronify.utils.MyDateTimeFormatter.toFriendlyString

object EditScheduleScreenDestination : NavigationDestination {
    override val titleRes = string.edit_title
    override val route = "edit"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleScreen(
    navigateBack: () -> Unit,
    viewModel: ScheduleEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    // 由于viewModel中的uiState是异步加载，而在异步任务完成之前，uiState 使用的是默认值 ScheduleUiState()
    // 检查 uiState 是否加载完成
    val isLoading = viewModel.uiState.schedule.id == 0 // 表示数据未加载
    if (isLoading) {
        // 显示加载状态
        Text(text = "Loading...", modifier = Modifier.fillMaxWidth())
    } else {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(string.edit_title),
                    centeredTitle = false,
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            enabled = viewModel.uiState.isValid,
                            onClick = {
                                coroutineScope.launch { viewModel.updateScheduleEntity() }
                                navigateBack()
                            }) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(string.submit)
                            )
                        }
                    },
                    scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                )
            }
        ) { innerPadding ->
            EditScreenBody(
                scheduleUiState = viewModel.uiState,
                onUiStateChange = viewModel::updateUiState,
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
}

@Composable
fun EditScreenBody(
    scheduleUiState: ScheduleUiState,
    onUiStateChange: (Schedule) -> Unit,
    onSubmit: () -> Unit = {},
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
    onSubmit: () -> Unit = {},
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
        // Log.d("EditScreen", "initialValue: ${schedule.type}")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnumDropdown(
                label = stringResource(string.type_req),
                initialValue = schedule.type,
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
                onCheckedChange = {
                    onValueChange(
                        schedule.copy(
                            isFinished = it,
                            endDT = System.currentTimeMillis()
                        )
                    )
                }
            )
        }
        // beginDT and endDT
        var beginDTMillis by remember { mutableStateOf(System.currentTimeMillis()) }
        var endDTMillis by remember { mutableStateOf(System.currentTimeMillis()) }
        var showBeginDTPicker by remember { mutableStateOf(false) }
        var showEndDTPicker by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = schedule.beginDT?.toFriendlyString() ?: "",
            onValueChange = { },
            label = { Text(stringResource(string.beginDT_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                // disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(beginDTMillis) {
                    awaitEachGesture {
                        // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                        // in the Initial pass to observe events before the text field consumes them
                        // in the Main pass.
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showBeginDTPicker = true
                        }
                    }
                },
        )
        if (showBeginDTPicker) {
            DateTimePickerDialog(
                onDismiss = { showBeginDTPicker = false },
                onConfirm = { begin, _ ->
                    onValueChange(schedule.copy(beginDT = begin))
                    showBeginDTPicker = false
                },
            )
        }

        OutlinedTextField(
            value = schedule.endDT?.toFriendlyString() ?: "",
            onValueChange = { },
            label = { Text(stringResource(string.endDT_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                // disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(endDTMillis) {
                    awaitEachGesture {
                        // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                        // in the Initial pass to observe events before the text field consumes them
                        // in the Main pass.
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showEndDTPicker = true
                        }
                    }
                },
        )
        if (showEndDTPicker) {
            DateTimePickerDialog(
                onDismiss = { showEndDTPicker = false },
                onConfirm = { end, _ ->
                    onValueChange(schedule.copy(endDT = end))
                    showEndDTPicker = false
                },
            )
        }

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