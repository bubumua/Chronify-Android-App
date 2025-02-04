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
import androidx.navigation.NavHostController
import myapp.chronify.R.dimen
import myapp.chronify.R.string
import myapp.chronify.ui.element.screen.HistoryScreenDestination
import myapp.chronify.ui.element.screen.ReminderScreenDestination

@Composable
fun NavDrawerContent(
    currentRoute: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // nav drawer header
        Text(
            text = stringResource(string.app_name), style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider()

        // todos
        Text(
            stringResource(string.todo_list),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        // reminder
        NavigationDrawerItem(label = { Text(stringResource(string.todo_reminder)) },
            selected = currentRoute == ReminderScreenDestination.route,
            onClick = {
                if (currentRoute != ReminderScreenDestination.route)
                    navController.navigate(ReminderScreenDestination.route)
            })
        // history
        NavigationDrawerItem(label = { Text(stringResource(string.todo_history)) },
            selected = currentRoute == HistoryScreenDestination.route,
            onClick = {
                if(currentRoute != HistoryScreenDestination.route)
                    navController.navigate(HistoryScreenDestination.route)
            })

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // statistics
        NavigationDrawerItem(label = { Text(stringResource(string.statistics)) },
            selected = false,
            onClick = { /* Handle click */ })
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // settings
        NavigationDrawerItem(label = { Text(stringResource(string.setting)) },
            selected = false,
            onClick = { /* Handle click */ })
    }
}