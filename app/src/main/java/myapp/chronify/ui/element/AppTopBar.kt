package myapp.chronify.ui.element

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    centeredTitle: Boolean = true,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier,
) {
    if (centeredTitle) {
        CenterAlignedTopAppBar(
            title = { Text(title) },
            modifier = modifier,
            actions = actions,
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon

            // {
            //     if (canNavigateBack) {
            //         IconButton(onClick = onBackClick) {
            //             Icon(
            //                 imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            //                 contentDescription = stringResource(
            //                     R.string.back
            //                 )
            //             )
            //         }
            //     }
            // }
        )
    } else {
        TopAppBar(
            title = { Text(title) },
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    }
}
