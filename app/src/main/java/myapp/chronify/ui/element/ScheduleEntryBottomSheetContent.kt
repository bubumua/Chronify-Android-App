package myapp.chronify.ui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myapp.chronify.R.dimen
import myapp.chronify.R.string
import myapp.chronify.R.drawable
import myapp.chronify.datamodel.Schedule
import myapp.chronify.datamodel.ScheduleType
import myapp.chronify.datamodel.getIcon
import myapp.chronify.datamodel.getLocalizedName
import myapp.chronify.ui.theme.bluesimple.BlueSimpleTheme
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import myapp.chronify.ui.viewmodel.ScheduleAddViewModel

@Composable
fun AddScheduleBottomSheetContent(
    viewModel: ScheduleAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onValueChange: (Schedule) -> Unit = viewModel::updateUiState,
    canSubmit: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val schedule = viewModel.scheduleUiState.schedule

    Column(modifier = modifier.fillMaxSize()) {
        // bottom sheet title and save button
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(string.add_schedule_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
            TextButton(
                enabled = canSubmit,
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveScheduleEntity()
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) { Text(stringResource(string.submit)) }
            // Button(onClick = {
            //     scope.launch { sheetState.hide() }.invokeOnCompletion {
            //         if (!sheetState.isVisible) {
            //             showBottomSheet = false
            //         }
            //     }
            // }) {
            //     Text("Hide bottom sheet")
            // }
        }
        // title and isFinished
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = schedule.title,
                onValueChange = { onValueChange(schedule.copy(title = it)) },
                label = { stringResource(string.title_req) },
                // colors = OutlinedTextFieldDefaults.colors(
                //     focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                //     unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                //     disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                // ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(dimensionResource(dimen.padding_tiny)))
            Checkbox(
                checked = schedule.isFinished,
                onCheckedChange = { onValueChange(schedule.copy(isFinished = it)) },
                modifier = Modifier.size(24.dp)
            )
        }
        // type, date&time picker
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TypeMenu(
                ScheduleType.REMINDER,
                onSelect = {
                    onValueChange(
                        schedule.copy(
                            type = it,
                            isFinished = when (it) {
                                ScheduleType.CHECK_IN -> true
                                else -> false
                            }
                        )
                    )
                }
            )
            AssistChip(
                onClick = {},
                label = { Text("Date & Time") },
                leadingIcon = {
                    Icon(
                        painterResource(drawable.calendar_add_on_24px),
                        contentDescription = "Date & Time",
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            )
        }
    }

}


@Composable
fun TypeMenu(
    initialType: ScheduleType,
    onSelect: (ScheduleType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(initialType) }
    val types = ScheduleType.entries

    Box(
        modifier = modifier
    ) {
        AssistChip(
            onClick = { expanded = !expanded },
            label = { Text(selectedType.getLocalizedName()) },
            leadingIcon = {
                Icon(
                    painterResource(selectedType.getIcon()),
                    contentDescription = selectedType.getLocalizedName(),
                    Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.getLocalizedName()) },
                    leadingIcon = {
                        Icon(
                            painterResource(type.getIcon()),
                            contentDescription = type.getLocalizedName(),
                        )
                    },
                    onClick = {
                        selectedType = type
                        onSelect(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TypeMenuPreview() {
    TypeMenu(ScheduleType.REMINDER)
}

@Preview(showBackground = true)
@Composable
fun AddScheduleBottomSheetContentPreview() {
    BlueSimpleTheme {
        AddScheduleBottomSheetContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddScheduleBottomSheetPreview() {
    BlueSimpleTheme {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
            modifier = Modifier.fillMaxHeight(),
        ) {
            // Sheet content
            // Button(onClick = {
            //     scope.launch { sheetState.hide() }.invokeOnCompletion {
            //         if (!sheetState.isVisible) {
            //             showBottomSheet = false
            //         }
            //     }
            // }) {
            //     Text("Hide bottom sheet")
            // }
            AddScheduleBottomSheetContent()
        }
    }


}
