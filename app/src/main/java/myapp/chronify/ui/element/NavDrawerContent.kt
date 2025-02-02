package myapp.chronify.ui.element

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import myapp.chronify.R.dimen
import myapp.chronify.R.string

@Composable
fun NavDrawerContent() {
    Column(
        modifier =
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(string.app_name),
            style = MaterialTheme.typography.titleLarge)

        HorizontalDivider()

        Text(stringResource(string.todo_list), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)

        NavigationDrawerItem(
            label = { Text(stringResource(string.todo_reminder)) },
            selected = false,
            onClick = { /* Handle click */ }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(string.todo_history)) },
            selected = false,
            onClick = { /* Handle click */ }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        NavigationDrawerItem(
            label = { Text(stringResource(string.statistics)) },
            selected = false,
            onClick = { /* Handle click */ }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        NavigationDrawerItem(
            label = { Text(stringResource(string.setting)) },
            selected = false,
            onClick = { /* Handle click */ }
        )
    }
}