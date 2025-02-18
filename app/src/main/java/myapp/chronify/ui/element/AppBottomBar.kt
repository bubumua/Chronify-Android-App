package myapp.chronify.ui.element

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import myapp.chronify.ui.navigation.NavigationRoute
import myapp.chronify.R.drawable
import myapp.chronify.R.string
import myapp.chronify.ui.element.screen.MarkerScreenRoute
import myapp.chronify.ui.element.screen.SettingsScreenRoute
import myapp.chronify.ui.element.screen.StatisticsScreenRoute

sealed class BottomNavItem(
    val screenTitleRes: Int,
    val iconRes: Int,
    val selectedIconRes: Int
) {

    object Marker : NavigationRoute, BottomNavItem(
        screenTitleRes = string.marker_title,
        iconRes = drawable.diagnosis_24px,
        selectedIconRes = drawable.filled_diagnosis_24px
    ) {
        override val route: String = MarkerScreenRoute.route
    }

    object Statistics : NavigationRoute, BottomNavItem(
        screenTitleRes = string.statistics,
        iconRes = drawable.leaderboard_24px,
        selectedIconRes = drawable.filled_leaderboard_24px
    ) {
        override val route: String = StatisticsScreenRoute.route
    }

    object Settings : NavigationRoute, BottomNavItem(
        screenTitleRes = string.setting,
        iconRes = drawable.settings_24px,
        selectedIconRes = drawable.filled_settings_24px
    ) {
        override val route: String = SettingsScreenRoute.route
    }
}

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit
) {
    val screens = listOf(
        BottomNavItem.Marker,
        BottomNavItem.Statistics,
        BottomNavItem.Settings
    )
    // var selectedItemIndex by remember { mutableIntStateOf(0) }

    NavigationBar {
        screens.forEachIndexed { _, item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    onNavigateToRoute(item.route)
                    // selectedItemIndex = index
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            if (currentRoute == item.route) item.selectedIconRes else item.iconRes
                        ),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(item.screenTitleRes)) }
            )
        }
    }
}