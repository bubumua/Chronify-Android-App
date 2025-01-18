package myapp.chronify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import myapp.chronify.ui.element.ScheduleAddScreen
import myapp.chronify.ui.element.AddScheduleScreenDestination
import myapp.chronify.ui.element.RemindScreen
import myapp.chronify.ui.element.RemindScreenDestination

@Composable
fun AppNavHost(
    navController: NavHostController= rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = RemindScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = RemindScreenDestination.route) {
            RemindScreen(navigateToAddScreen = { navController.navigate(AddScheduleScreenDestination.route) })
        }
        composable(route = AddScheduleScreenDestination.route) {
            ScheduleAddScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}