package myapp.chronify.ui.element

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


enum class SwipeToRevealValue { Read, Resting, Delete }

@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryBook3(
    onClickRead: () -> Unit={ Log.d("LibraryBook3", "onClickRead")},
    onClickDelete: () -> Unit={Log.d("LibraryBook3", "onClickDelete")},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val dragState = remember {
        // define the drag offset for the actions
        val actionOffset = with(density) { 100.dp.toPx() }
        // normally, just need to adjust anchors to the action offset
        AnchoredDraggableState(
            initialValue = SwipeToRevealValue.Resting,
            anchors = DraggableAnchors {
                SwipeToRevealValue.Delete at actionOffset
                SwipeToRevealValue.Resting at 0f
                // ScheduleItemSwipeAnchorValue.Read at -actionOffset
            },
            positionalThreshold = { distance -> distance * 1.0f },
            velocityThreshold = { with(density) { 50.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec,
        )
    }

    // show a bit overscroll ui effect
    // val overScrollEffect = ScrollableDefaults.overscrollEffect()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content that moves with the swipe
        Box(
            modifier = Modifier
                .anchoredDraggable(
                    dragState,
                    Orientation.Horizontal,
                    // overscrollEffect = overScrollEffect
                )
                // .overscroll(overScrollEffect)
                .offset {
                    IntOffset(
                        x = dragState.requireOffset().roundToInt(),
                        y = 0
                    )
                }
                .background(Color.Gray)
                .matchParentSize()
        ) {
            Text("Center")
        }

        // actions container
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delete Action
            AnimatedVisibility(
                visible = dragState.currentValue == SwipeToRevealValue.Delete,
                enter = slideInHorizontally(animationSpec = tween()) { -it },
                exit = slideOutHorizontally(animationSpec = tween()) { -it }
            ) {
                IconButton(onClick = onClickDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Read Action
            AnimatedVisibility(
                visible = dragState.currentValue == SwipeToRevealValue.Read,
                enter = slideInHorizontally(animationSpec = tween()) { it },
                exit = slideOutHorizontally(animationSpec = tween()) { it }
            ) {
                IconButton(onClick = onClickRead) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Read"
                    )
                }
            }



        }
    }

    // Side effect to launch the animation when the drag state changes
    // LaunchedEffect(dragState) {
    //     snapshotFlow { dragState.settledValue }
    //         .collectLatest {
    //             when (it) {
    //                 SwipeToRevealValue.Read -> onClickRead()
    //                 SwipeToRevealValue.Delete -> onClickDelete()
    //                 else -> {}
    //             }
    //             delay(30)
    //             dragState.animateTo(SwipeToRevealValue.Resting)
    //         }
    // }
}