package myapp.chronify.ui.element

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import myapp.chronify.R.string
import myapp.chronify.R.dimen
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.datamodel.ScheduleType
import myapp.chronify.ui.navigation.NavigationDestination
import myapp.chronify.ui.theme.BusScheduleTheme
import myapp.chronify.ui.viewmodel.RemindViewModel
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import kotlin.math.roundToInt

object RemindScreenDestination : NavigationDestination {
    override val route = "mark"
    override val titleRes = string.app_name
}

enum class ScheduleItemSwipeAnchorValue { Read, Resting, Delete }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindScreen(
    navigateToAddScreen: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: RemindViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val remindUiState by viewModel.remindUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = stringResource(string.app_name),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                // onClick = navigateToAddScreen,
                onClick = { showBottomSheet = true },
                modifier = Modifier.padding(dimensionResource(dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(string.add_button)
                )
            }
        }
    ) { innerPadding ->
        RemindBody(
            scheduleList = remindUiState.scheduleList,
            onListItemClick = {},
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }) {
                    Text("Hide bottom sheet")
                }
            }
        }
    }
}

@Composable
fun RemindBody(
    scheduleList: List<ScheduleEntity> = emptyList(),
    onListItemClick: (Int) -> Unit = {},
    viewModel: RemindViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        // Text(
        //     "there is schedule plan",
        //     style = MaterialTheme.typography.titleLarge,
        //     modifier = Modifier
        //         .fillMaxWidth()
        //         .padding(contentPadding)
        // )
        if (scheduleList.isEmpty()) {
            Text(
                text = stringResource(string.tip_no_schedule),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            ScheduleList(
                itemList = scheduleList,
                onItemClick = { onListItemClick(it.id) },
                viewModel = viewModel,
                coroutineScope = coroutineScope,
                modifier = Modifier.padding(horizontal = dimensionResource(dimen.padding_small)),
                contentPadding = contentPadding,
            )
        }
    }

}

@Composable
fun ScheduleList(
    itemList: List<ScheduleEntity> = emptyList(),
    viewModel: RemindViewModel = viewModel(factory = AppViewModelProvider.Factory),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onItemClick: (ScheduleEntity) -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = itemList, key = { it.id }) { item ->
            ScheduleItem(
                item = item,
                onDelete = {
                    Log.d("ScheduleList", "Delete item: ${item.title}")
                    coroutineScope.launch {
                        viewModel.deleteSchedule(item)
                    }
                },
                modifier = Modifier
                    .padding(dimensionResource(dimen.padding_tiny))
                    .clickable { onItemClick(item) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleItem(
    item: ScheduleEntity,
    onCheck: (ScheduleEntity) -> Unit = {},
    onDelete: (ScheduleEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {

    val density = LocalDensity.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val dragState = remember {
        // define the drag offset for the actions
        val actionOffset = with(density) { 250.dp.toPx() }
        // normally, just need to adjust anchors to the action offset
        AnchoredDraggableState(
            initialValue = ScheduleItemSwipeAnchorValue.Resting,
            anchors = DraggableAnchors {
                // ScheduleItemSwipeAnchorValue.Read at actionOffset
                ScheduleItemSwipeAnchorValue.Resting at 0f
                ScheduleItemSwipeAnchorValue.Delete at -actionOffset
            },
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 150.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec,
        )
    }

    // show a bit overscroll ui effect
    val overScrollEffect = ScrollableDefaults.overscrollEffect()

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // swipe-able main content
        Card(
            modifier = modifier
                .anchoredDraggable(
                    dragState,
                    Orientation.Horizontal,
                    overscrollEffect = overScrollEffect
                )
                .overscroll(overScrollEffect)
                .offset {
                    IntOffset(
                        x = dragState.requireOffset().roundToInt(),
                        y = 0
                    )
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                    Text(text = item.type)
                }
                Checkbox(
                    checked = item.isFinished,
                    onCheckedChange = { onCheck(item) }
                )
            }
        }

        // actions container
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // On the left: other Action
            AnimatedVisibility(
                visible = dragState.currentValue == ScheduleItemSwipeAnchorValue.Read,
                enter = slideInHorizontally(animationSpec = tween()) { it },
                exit = slideOutHorizontally(animationSpec = tween()) { it }
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Read"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // On the right: Delete Action
            AnimatedVisibility(
                visible = dragState.currentValue == ScheduleItemSwipeAnchorValue.Delete,
                enter = slideInHorizontally(animationSpec = tween()) { it },
                exit = slideOutHorizontally(animationSpec = tween()) { it }
            ) {
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }


    }
    // Side effect to launch the animation when the drag state changes
    LaunchedEffect(dragState) {
        snapshotFlow { dragState.settledValue }
            .collectLatest {
                when (it) {
                    // ScheduleItemSwipeAnchorValue.Read -> onClickRead()
                    ScheduleItemSwipeAnchorValue.Delete -> onDelete(item)
                    else -> {}
                }
                delay(30)
                dragState.animateTo(ScheduleItemSwipeAnchorValue.Resting)
            }
    }

}

@Preview(showBackground = true)
@Composable
fun RemindBodyPreview() {
    BusScheduleTheme {
        RemindBody(
            listOf(
                ScheduleEntity(1, "Drink", ScheduleType.DEFAULT.name, false),
                ScheduleEntity(2, "Eat", ScheduleType.DEFAULT.name, false),
                ScheduleEntity(3, "Sleep", ScheduleType.DEFAULT.name, false),
            ),
            onListItemClick = {}
        )
    }
}