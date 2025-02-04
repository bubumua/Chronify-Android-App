package myapp.chronify.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import myapp.chronify.R.string
import myapp.chronify.utils.combineDateTimeState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?) -> Unit = { _, _ -> onDismiss() }
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                // .width(IntrinsicSize.Min)
                .fillMaxWidth()
                // .height(IntrinsicSize.Min)
                // .height(600.dp)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp),
            ) {

                var selectedIndex by remember { mutableIntStateOf(0) }
                val options = listOf("Date", "Time")

                SingleChoiceSegmentedButtonRow {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = { selectedIndex = index },
                            selected = index == selectedIndex,
                        ) { Text(label) }
                    }
                }

                var selectedDateRange by remember { mutableStateOf(DateRange()) }
                val calender = Calendar.getInstance()
                val timePickerState = rememberTimePickerState(
                    initialHour = calender.get(Calendar.HOUR_OF_DAY),
                    initialMinute = calender.get(Calendar.MINUTE),
                    is24Hour = false,
                )

                when (selectedIndex) {
                    // pick date
                    0 -> {
                        SimpleDateRangePicker(
                            initialDateRange = selectedDateRange,
                            startFromSunday = false,
                            onDateRangeSelected = { dr ->
                                selectedDateRange = dr
                            },
                        )
                    }
                    // pick time
                    1 -> {
                        TimePolymer(timePickerState)
                    }

                    else -> {}
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text(stringResource(string.cancel)) }
                    TextButton(
                        onClick = {
                            onConfirm(
                                if (selectedDateRange.startDate != null) {
                                    combineDateTimeState(
                                        selectedDateRange.startDate!!,
                                        timePickerState
                                    )
                                } else {
                                    null
                                },
                                if (selectedDateRange.endDate != null) {
                                    combineDateTimeState(
                                        selectedDateRange.endDate!!,
                                        timePickerState
                                    )
                                } else {
                                    null
                                }
                            )
                        }
                    ) { Text(stringResource(string.confirm)) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateTimePickerDialogPreview() {
    DateTimePickerDialog(
        onDismiss = {}
    )
}
