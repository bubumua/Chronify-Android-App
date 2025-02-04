package myapp.chronify.ui.element.screen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import myapp.chronify.R.dimen
import myapp.chronify.R.string
import myapp.chronify.data.schedule.ScheduleEntity
import myapp.chronify.datamodel.Schedule
import myapp.chronify.datamodel.ScheduleType
import myapp.chronify.datamodel.getLocalizedName
import myapp.chronify.ui.element.AddScheduleBottomSheet
import myapp.chronify.ui.element.AppTopBar
import myapp.chronify.ui.element.NavDrawerContent
import myapp.chronify.ui.navigation.NavigationDestination
import myapp.chronify.ui.theme.BusScheduleTheme
import myapp.chronify.ui.viewmodel.AppViewModelProvider
import myapp.chronify.ui.viewmodel.ScheduleListViewModel
import myapp.chronify.utils.MyDateTimeFormatter.toFriendlyString
import kotlin.math.roundToInt
import myapp.chronify.utils.toSchedule

object ReminderScreenDestination : NavigationDestination {
    override val route = "mark"
    override val titleRes = string.app_name
}

enum class ScheduleItemSwipeAnchorValue { Read, Resting, Delete }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavHostController,
    navigateToEdit: (Int) -> Unit = {},
    viewModel: ScheduleListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    val remindUiState by viewModel.remindUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavDrawerContent(ReminderScreenDestination.route,navController=navController)
            }
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppTopBar(
                    title = stringResource(string.app_name),
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
                            Icon(Icons.Default.Menu, contentDescription = stringResource(string.menu))
                        }
                    },
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
            ReminderBody(
                scheduleList = remindUiState.scheduleList,
                onListItemClick = navigateToEdit,
                coroutineScope = scope,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding
            )
            // Bottom sheet
            if (showBottomSheet) {
                AddScheduleBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ReminderBody(
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
        // TODO: a visualization schedule

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
    viewModel: ScheduleListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onItemClick: (ScheduleEntity) -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    // TODO: pull to refresh
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = itemList, key = { it.id }) { item ->
            ScheduleItem(
                item = item,
                onDelete = {
                    // Log.d("ScheduleList", "Delete item: ${item.title}")
                    coroutineScope.launch {
                        viewModel.deleteSchedule(item)
                    }
                },
                onCheck = {
                    // Log.d("ScheduleList", "Check item: ${item.title}")
                    coroutineScope.launch {
                        viewModel.updateSchedule(
                            it.copy(
                                isFinished = !it.isFinished,
                                endDT = System.currentTimeMillis()
                            )
                        )
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
private fun ScheduleItem(
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                    if (item.beginDT != null || item.endDT != null) {
                        ScheduleDTText(item)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // type text
                    Text(
                        text = item.toSchedule().type.getLocalizedName(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    // isFinished checkbox
                    Checkbox(
                        checked = item.isFinished,
                        onCheckedChange = { onCheck(item) }
                    )
                }
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

@Composable
fun ScheduleDTText(
    schedule: Schedule,
    placeholderStr: String = "",
    modifier: Modifier = Modifier
) {
    val annotatedString = buildAnnotatedString {
        if (schedule.beginDT == null && schedule.endDT == null) {
            append(placeholderStr)
        } else {
            if (schedule.beginDT != null) {
                if (schedule.beginDT <= System.currentTimeMillis()) {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append(schedule.beginDT.toFriendlyString())
                    }
                } else {
                    append(schedule.beginDT.toFriendlyString())
                }
            } else {
                append("?")
            }
            if (schedule.endDT != null && schedule.endDT != schedule.beginDT) {
                append(" - ")
                append(schedule.endDT.toFriendlyString())
            }
        }
    }
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
    )
}

@Composable
fun ScheduleDTText(
    schedule: ScheduleEntity,
    placeholderStr: String = "",
    modifier: Modifier = Modifier
) {
    ScheduleDTText(schedule.toSchedule(), placeholderStr, modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun RemindBodyPreview() {
    BusScheduleTheme {
        ReminderBody(
            listOf(
                ScheduleEntity(1, "Drink", ScheduleType.DEFAULT.name, false),
                ScheduleEntity(2, "Eat", ScheduleType.DEFAULT.name, false),
                ScheduleEntity(3, "Sleep", ScheduleType.DEFAULT.name, false),
            ),
            onListItemClick = {}
        )
    }
}