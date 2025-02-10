package myapp.chronify.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import myapp.chronify.ui.element.screen.EditScheduleScreen
import myapp.chronify.ui.element.screen.EditScheduleScreenRoute
import myapp.chronify.ui.element.screen.HistoryScreen
import myapp.chronify.ui.element.screen.HistoryScreenRoute
import myapp.chronify.ui.element.screen.JournalScreen
import myapp.chronify.ui.element.screen.JournalScreenRoute
import myapp.chronify.ui.element.screen.SettingsScreen
import myapp.chronify.ui.element.screen.SettingsScreenRoute
import myapp.chronify.ui.element.screen.StatisticsScreen
import myapp.chronify.ui.element.screen.StatisticsScreenRoute

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    NavHost(
        navController = navController,
        startDestination = JournalScreenRoute.route,
        modifier = modifier
    ) {
        composable(route = JournalScreenRoute.route) {
            // RemindScreen(navigateToAddScreen = { navController.navigate(AddScheduleScreenRoute.route) })
            JournalScreen(
                navController = navController,
                navigateToEdit = { navController.navigate("${EditScheduleScreenRoute.route}/$it") }
            )

            // 拦截返回键事件
            // BackHandler {
            //     // 退出应用程序
            //     activity?.finish()
            // }
        }
        composable(route = HistoryScreenRoute.route) {
            HistoryScreen(
                navController = navController,
                navigateToEdit = { navController.navigate("${EditScheduleScreenRoute.route}/$it") })
        }

        // composable(route = AddScheduleScreenRoute.route) {
        //     ScheduleAddScreen(
        //         navigateBack = { navController.popBackStack() },
        //         onNavigateUp = { navController.navigateUp() }
        //     )
        // }

        composable(
            route = EditScheduleScreenRoute.routeWithArgs,
            arguments = listOf(navArgument(EditScheduleScreenRoute.itemIdArg) {
                type =
                    NavType.IntType
            })
        ) {
            EditScheduleScreen(navigateBack = { navController.navigateUp() })
        }

        composable(route = StatisticsScreenRoute.route) {
            StatisticsScreen()
        }

        composable(route = SettingsScreenRoute.route) {
            SettingsScreen()
        }
    }
}