package myapp.chronify.ui.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import myapp.chronify.ui.element.screen.ScheduleAddScreen
import myapp.chronify.ui.element.screen.AddScheduleScreenDestination
import myapp.chronify.ui.element.screen.EditScheduleScreen
import myapp.chronify.ui.element.screen.EditScheduleScreenDestination
import myapp.chronify.ui.element.screen.HistoryScreen
import myapp.chronify.ui.element.screen.HistoryScreenDestination
import myapp.chronify.ui.element.screen.ReminderScreen
import myapp.chronify.ui.element.screen.ReminderScreenDestination

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    NavHost(
        navController = navController,
        startDestination = ReminderScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = ReminderScreenDestination.route) {
            // RemindScreen(navigateToAddScreen = { navController.navigate(AddScheduleScreenDestination.route) })
            ReminderScreen(
                navController = navController,
                navigateToEdit = { navController.navigate("${EditScheduleScreenDestination.route}/$it") })

            // 拦截返回键事件
            BackHandler {
                // 退出应用程序
                activity?.finish()
            }
        }
        composable(route = HistoryScreenDestination.route) {
            HistoryScreen(
                navController = navController,
                navigateToEdit = { navController.navigate("${EditScheduleScreenDestination.route}/$it") })
        }
        // composable(route = AddScheduleScreenDestination.route) {
        //     ScheduleAddScreen(
        //         navigateBack = { navController.popBackStack() },
        //         onNavigateUp = { navController.navigateUp() }
        //     )
        // }
        composable(
            route = EditScheduleScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(EditScheduleScreenDestination.itemIdArg) {
                type =
                    NavType.IntType
            })
        ) {
            EditScheduleScreen(navigateBack = { navController.navigateUp() })
        }
    }
}