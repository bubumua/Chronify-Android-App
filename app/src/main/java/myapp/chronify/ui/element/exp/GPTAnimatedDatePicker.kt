package myapp.chronify.ui.element.exp


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


// @Composable
// fun AnimatedDateRangePicker(
//     startFromSunday: Boolean = false,
//     onDateRangeSelected: (DateRange) -> Unit
// ) {
//     var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
//     var dateRange by remember { mutableStateOf(DateRange()) }
//
//     // 动画状态
//     val animatedOffset = remember { Animatable(0f) }
//     // 记录正在切换的方向 (1: 向下滑动显示上个月, -1: 向上滑动显示下个月)
//     var animationDirection by remember { mutableStateOf(0) }
//     // 存储三个月的数据（上月、当月、下月）
//     val monthsList = remember(currentYearMonth) {
//         listOf(
//             currentYearMonth.minusMonths(1),
//             currentYearMonth,
//             currentYearMonth.plusMonths(1)
//         )
//     }
//
//     val coroutineScope = rememberCoroutineScope()
//
//     Column(
//         modifier = Modifier
//             .fillMaxWidth()
//             .padding(16.dp)
//     ) {
//         // 顶部控制栏
//         CalendarHeader(
//             currentYearMonth = currentYearMonth,
//             onMonthChange = { newYearMonth ->
//                 coroutineScope.launch {
//                     // 根据月份变化方向设置动画
//                     animationDirection = if (newYearMonth > currentYearMonth) -1 else 1
//                     animatedOffset.snapTo(animationDirection * 100f)
//                     currentYearMonth = newYearMonth
//                     animatedOffset.animateTo(
//                         targetValue = 0f,
//                         animationSpec = tween(300, easing = FastOutSlowInEasing)
//                     )
//                     animationDirection = 0
//                 }
//             }
//         )
//
//         // 星期标题行
//         DayOfWeekHeader(startFromSunday = startFromSunday)
//
//         // 可滑动的日历主体
//         Box(
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .pointerInput(Unit) {
//                     detectVerticalDragGestures(
//                         onDragEnd = {
//                             coroutineScope.launch {
//                                 // 判断是否需要切换月份
//                                 if (abs(animatedOffset.value) > 50) {
//                                     val direction = if (animatedOffset.value > 0) 1 else -1
//                                     currentYearMonth = if (direction > 0) {
//                                         currentYearMonth.minusMonths(1)
//                                     } else {
//                                         currentYearMonth.plusMonths(1)
//                                     }
//                                     animatedOffset.animateTo(
//                                         targetValue = 0f,
//                                         animationSpec = tween(300, easing = FastOutSlowInEasing)
//                                     )
//                                 } else {
//                                     // 回弹动画
//                                     animatedOffset.animateTo(
//                                         targetValue = 0f,
//                                         animationSpec = spring(
//                                             dampingRatio = Spring.DampingRatioMediumBouncy,
//                                             stiffness = Spring.StiffnessLow
//                                         )
//                                     )
//                                 }
//                             }
//                         },
//                         onDragCancel = {
//                             coroutineScope.launch {
//                                 animatedOffset.animateTo(0f)
//                             }
//                         },
//                         onVerticalDrag = { change, dragAmount ->
//                             coroutineScope.launch {
//                                 animatedOffset.snapTo(animatedOffset.value + dragAmount)
//                             }
//                             change.consume()
//                         }
//                     )
//                 }
//         ) {
//             // 动画包装的日历网格
//             Box(
//                 modifier = Modifier.offset {
//                     IntOffset(0, animatedOffset.value.roundToInt())
//                 }
//             ) {
//                 monthsList.forEachIndexed { index, yearMonth ->
//                     var itemHeight by remember { mutableStateOf(0) }
//                     Box(
//                         modifier = Modifier
//                             .onSizeChanged { size ->
//                                 itemHeight = size.height
//                                 Log.d("AnimatedDateRangePicker", "itemHeight: $itemHeight")
//                             }
//                             .offset {
//                                 IntOffset(
//                                     0,
//                                     ((index - 1) * 465 - animatedOffset.value).roundToInt()
//                                 )
//                             }
//                     ) {
//                         DaysGrid(
//                             yearMonth = yearMonth,
//                             dateRange = dateRange,
//                             startFromSunday = startFromSunday,
//                             onDateSelected = { selectedDate ->
//                                 // 日期选择逻辑保持不变
//                                 dateRange = when {
//                                     dateRange.startDate == selectedDate &&
//                                             dateRange.endDate == null -> DateRange()
//
//                                     dateRange.startDate == null ->
//                                         DateRange(startDate = selectedDate)
//
//                                     dateRange.endDate == null -> {
//                                         if (selectedDate.isBefore(dateRange.startDate)) {
//                                             DateRange(startDate = selectedDate)
//                                         } else {
//                                             DateRange(
//                                                 startDate = dateRange.startDate,
//                                                 endDate = selectedDate
//                                             )
//                                         }
//                                     }
//
//                                     else -> DateRange(startDate = selectedDate)
//                                 }
//                                 onDateRangeSelected(dateRange)
//                             }
//                         )
//                     }
//                 }
//             }
//         }
//     }
// }


// @Preview(showBackground = true)
// @Composable
// fun AnimatedDateRangePickerPreview() {
//     var selectedDateRange by remember { mutableStateOf(DateRange()) }
//
//     Column {
//         // 显示选中的日期范围
//         Column(modifier = Modifier.padding(16.dp)) {
//             Text("Start Date: ${selectedDateRange.startDate}")
//             Text("End Date: ${selectedDateRange.endDate}")
//         }
//         AnimatedDateRangePicker(
//             startFromSunday = false,
//             onDateRangeSelected = { dateRange ->
//                 selectedDateRange = dateRange
//             }
//         )
//     }
// }

@Preview(showBackground = true)
@Composable
fun DaysGridPreview() {
    val shortText = "Hi"
    val longText = "Very long text\nthat spans across\nmultiple lines"
    var short by remember { mutableStateOf(true) }
    Box(modifier =
    Modifier
        .background(Color.Blue, RoundedCornerShape(15.dp))
        .clickable { short = !short }
        .padding(20.dp)
        .wrapContentSize()
        .animateContentSize()) {
        Text(
            if (short) {
                shortText
            } else {
                longText
            }, style = LocalTextStyle.current.copy(color = Color.White)
        )
    }
}