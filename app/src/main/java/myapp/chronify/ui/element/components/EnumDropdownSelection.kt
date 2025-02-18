package myapp.chronify.ui.element.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import myapp.chronify.data.nife.NifeType


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
        initialValue = NifeType.DEFAULT,
        onValueSelected = {}
    )
}

