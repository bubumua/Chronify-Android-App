package myapp.chronify.ui.element.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import myapp.chronify.R.drawable
import myapp.chronify.R.string


/** Represents the different modes that a date picker can be at. */
@Immutable
@JvmInline
@ExperimentalMaterial3Api
value class TimePolymerMode internal constructor(internal val value: Int) {

    companion object {
        /** Date picker mode */
        val Picker = TimePolymerMode(0)

        /** Date text input mode */
        val Input = TimePolymerMode(1)
    }

    override fun toString() =
        when (this) {
            Picker -> "Picker"
            Input -> "Input"
            else -> "Unknown"
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePolymer(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    title: @Composable (Modifier) -> Unit = {
        Text(
            "Time Picker",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
        )
    },
    headline: @Composable () -> Unit = { HorizontalDivider(thickness = 2.dp) },
) {
    var displayMode by remember { mutableStateOf(TimePolymerMode.Picker) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth(),
        ) {
            title(Modifier.weight(1f))
            // Switch(
            //     checked = state.is24hour,
            //     onCheckedChange = { state.is24hour = it },
            //     modifier = modifier
            // )
            when (displayMode) {
                TimePolymerMode.Picker -> {
                    IconButton(
                        onClick = { displayMode = TimePolymerMode.Input },
                        modifier = Modifier
                    ) {
                        Icon(
                            painterResource(drawable.filled_nest_clock_farsight_digital_24px),
                            contentDescription = stringResource(string.input_time),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                TimePolymerMode.Input -> {
                    IconButton(
                        onClick = { displayMode = TimePolymerMode.Picker },
                        modifier = Modifier
                    ) {
                        Icon(
                            painterResource(drawable.filled_nest_clock_farsight_analog_24px),
                            contentDescription = stringResource(string.pick_time),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
        headline()
        when (displayMode) {
            TimePolymerMode.Picker -> {
                TimePicker(state)
            }

            TimePolymerMode.Input -> {
                TimeInput(state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TimePolymerPreview() {
    val state = rememberTimePickerState()
    TimePolymer(state = state)
}

