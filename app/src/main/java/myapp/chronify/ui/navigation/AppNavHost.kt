package myapp.chronify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import myapp.chronify.ui.element.screen.ScheduleAddScreen
import myapp.chronify.ui.element.screen.AddScheduleScreenDestination
import myapp.chronify.ui.element.screen.ReminderScreen
import myapp.chronify.ui.element.screen.ReminderScreenDestination

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ReminderScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = ReminderScreenDestination.route) {
            // RemindScreen(navigateToAddScreen = { navController.navigate(AddScheduleScreenDestination.route) })
            ReminderScreen()
        }
        composable(route = AddScheduleScreenDestination.route) {
            ScheduleAddScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}