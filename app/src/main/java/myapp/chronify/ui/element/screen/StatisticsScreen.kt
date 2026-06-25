package myapp.chronify.ui.element.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import myapp.chronify.R.dimen
import myapp.chronify.R.string
import myapp.chronify.data.nife.Nife
import myapp.chronify.ui.element.ScrollableEventCalendar
import myapp.chronify.ui.element.ScrollableHistogram
import myapp.chronify.ui.element.components.AppTopBar
import myapp.chronify.ui.element.components.LazyPagingView
import myapp.chronify.ui.element.components.OutLinedTextFieldWithSuggestion
import myapp.chronify.ui.navigation.NavigationRoute
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import myapp.chronify.ui.viewmodel.StatisticsViewModel
import java.time.LocalDate

object StatisticsScreenRoute : NavigationRoute {
    override val route = "statistics"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToEdit: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()
    val lazyItems = viewModel.nifesPagingData.collectAsLazyPagingItems()
    val itemSnapshotList = lazyItems.itemSnapshotList
    val dateEventMap = remember(itemSnapshotList) {
        viewModel.convertToDateEventMap(itemSnapshotList.items)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = stringResource(string.statistics),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        StatisticsContent(
            uiState = uiState,
            lazyItems = lazyItems,
            dateEventMap = dateEventMap,
            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
            onLoadMoreEvents = {
                val appendState = lazyItems.loadState.append
                val canRequestMore =
                    lazyItems.itemCount > 0 &&
                        lazyItems.loadState.refresh !is LoadState.Loading &&
                        appendState !is LoadState.Loading &&
                        appendState !is LoadState.Error &&
                        (appendState as? LoadState.NotLoading)?.endOfPaginationReached != true

                if (canRequestMore) {
                    lazyItems[lazyItems.itemCount - 1]
                }
            },
            navigateToEdit = navigateToEdit,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
fun StatisticsContent(
    uiState: StatisticsViewModel.StatisticsUiState,
    lazyItems: LazyPagingItems<Nife>,
    dateEventMap: Map<LocalDate, List<Nife>>,
    onSearchQueryChange: (String) -> Unit,
    onLoadMoreEvents: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {

    Column(
        modifier = modifier
            .padding(contentPadding)
            .padding(dimensionResource(dimen.padding_small))
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 搜索框
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutLinedTextFieldWithSuggestion(
                initialValue = uiState.searchQuery,
                suggestions = uiState.suggestions,
                onValueChange = {
                    onSearchQueryChange(it)
                },
                label = { Text(stringResource(string.search_schedule)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            // 视图切换按钮
            // IconButton(onClick = { viewModel.toggleViewMode() }) {
            //     Icon(
            //         imageVector =
            //         if (uiState.isCalendarView)
            //             Icons.Default.ShoppingCart
            //         else Icons.Default.DateRange,
            //         contentDescription = stringResource(string.toggle_statistics)
            //     )
            // }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyPagingView(
            lazyItems = lazyItems,
            appendLoadingContent = {
                CircularProgressIndicator(
                    modifier = Modifier.padding(dimensionResource(dimen.padding_small))
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            ScrollableEventCalendar(
                markers = dateEventMap,
                onMenuItemClick = { nife -> navigateToEdit(nife.id) },
                onLoadMore = onLoadMoreEvents,
                setActiveColor = { count ->
                    val max = 4
                    val ratio = (count.coerceAtMost(max).toFloat() / max).coerceIn(0f, 1f)
                    lerp(
                        start = Color.LightGray.copy(alpha = .4f),
                        stop = Color.Green.copy(alpha = .8f),
                        fraction = ratio
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        ScrollableHistogram(
            data = uiState.monthCount,
            title = {
                Text(
                    stringResource(string.monthly_count_chart),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreen()
}
