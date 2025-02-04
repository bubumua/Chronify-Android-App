package myapp.chronify.ui.element

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import myapp.chronify.datamodel.ScheduleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTypeDropdown(
    selectedType: ScheduleType,
    onTypeSelected: (ScheduleType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val types = ScheduleType.entries
    Log.d("ScheduleTypeDropdown", "selectedType: $types")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedType.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Schedule Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleTypeDropdownPreview() {
    var selectedType by remember { mutableStateOf(ScheduleType.DEFAULT) }

    ScheduleTypeDropdown(
        selectedType = selectedType,
        onTypeSelected = { selectedType = it }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T : Enum<T>> EnumDropdown(
    label: String,
    initialValue: T,
    crossinline onValueSelected: (T) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val options = enumValues<T>().toList()
    var expanded by remember { mutableStateOf(false) }
    var selectedEnumItem by remember { mutableStateOf(initialValue) }

    // Log.d("EnumDropdown", "selectedEnumItem: $selectedEnumItem")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle         // expanding/ collapsing the menu on click. A read-only text field has         // the anchor type `PrimaryNotEditable`.
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            label = { Text(text = label) },
            value = selectedEnumItem.name,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            // colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.name,
                            // style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        selectedEnumItem = option
                        onValueSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnumDropdown2Preview() {
    EnumDropdown(
        label = "Type",
        initialValue = ScheduleType.DEFAULT,
        onValueSelected = {}
    )
}

