package myapp.chronify.ui.element.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import myapp.chronify.R
import myapp.chronify.R.string
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.ui.element.AppTopBar
import myapp.chronify.ui.element.NavDrawerContent
import myapp.chronify.ui.navigation.NavigationDestination
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import myapp.chronify.ui.viewmodel.ScheduleListViewModel

object HistoryScreenDestination : NavigationDestination {
    override val route = "history"
    override val titleRes = string.todo_history
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    navigateToEdit: (Int) -> Unit = {},
    viewModel: ScheduleListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    val historyUiState by viewModel.historyUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavDrawerContent(HistoryScreenDestination.route, navController = navController)
            }
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppTopBar(
                    title = stringResource(string.todo_history),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(string.menu)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { innerPadding ->
            HistoryBody(
                scheduleList = historyUiState.scheduleList,
                onListItemClick = navigateToEdit,
                coroutineScope = scope,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding
            )
        }
    }
}

@Composable
private fun HistoryBody(
    scheduleList: List<ScheduleEntity> = emptyList(),
    onListItemClick: (Int) -> Unit = {},
    viewModel: ScheduleListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        ScheduleList(
            itemList = scheduleList,
            onItemClick = { onListItemClick(it.id) },
            viewModel = viewModel,
            coroutineScope = coroutineScope,
            ifRenderOutdated = false,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small)),
            contentPadding = contentPadding,
        )
    }
}