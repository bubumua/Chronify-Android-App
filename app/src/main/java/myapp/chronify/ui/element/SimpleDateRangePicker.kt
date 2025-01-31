package myapp.chronify.ui.element


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

// 日期范围数据类
data class DateRange(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

// 附带额外信息的日期数据类
data class DateWithInfo(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)

@Composable
fun SimpleDateRangePicker(
    startFromSunday: Boolean = false,
    onDateRangeSelected: (DateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var dateRange by remember { mutableStateOf(DateRange()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 顶部控制栏
        CalendarHeader(
            currentYearMonth = currentYearMonth,
            onMonthChange = { currentYearMonth = it }
        )

        // 星期标题行
        DayOfWeekHeader(startFromSunday = startFromSunday)

        // 日历主体
        DaysGrid(
            yearMonth = currentYearMonth,
            dateRange = dateRange,
            startFromSunday = startFromSunday,
            onDateSelected = { selectedDate ->
                dateRange = when {
                    // 没有选择任何日期，或点击已选择的开始日期
                    dateRange.startDate == selectedDate && dateRange.endDate == null ->
                        DateRange()
                    // 第一次选择日期
                    dateRange.startDate == null ->
                        DateRange(startDate = selectedDate)
                    // 已有开始日期，没有结束日期
                    dateRange.endDate == null -> {
                        if (selectedDate.isBefore(dateRange.startDate)) {
                            DateRange(startDate = selectedDate)
                        } else {
                            DateRange(startDate = dateRange.startDate, endDate = selectedDate)
                        }
                    }
                    // 已有完整日期范围，重新开始选择
                    else -> DateRange(startDate = selectedDate)
                }
                onDateRangeSelected(dateRange)
            }
        )
    }
}

@Composable
private fun CalendarHeader(
    currentYearMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            // .padding(bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 年月选择菜单
        YearMonthMenu(
            currentYearMonth = currentYearMonth,
            onYearMonthSelected = onMonthChange
        )

        // 月份切换按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = {
                onMonthChange(currentYearMonth.minusMonths(1))
            }) {
                Icon(Icons.Default.KeyboardArrowLeft, "Previous month")
            }

            IconButton(onClick = {
                onMonthChange(currentYearMonth.plusMonths(1))
            }) {
                Icon(Icons.Default.KeyboardArrowRight, "Next month")
            }
        }
    }
}

@Composable
private fun YearMonthMenu(
    currentYearMonth: YearMonth,
    onYearMonthSelected: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSelectorExpended by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        ElevatedFilterChip(
            onClick = { isSelectorExpended = !isSelectorExpended },
            label = {
                Text(
                    text = currentYearMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            selected = isSelectorExpended,
        )
        DropdownMenu(
            expanded = isSelectorExpended,
            onDismissRequest = { isSelectorExpended = false }
        ) {
            YearMonthScrollableSelector(
                currentYearMonth = currentYearMonth,
                onYearMonthSelected = {
                    onYearMonthSelected(it)
                }
            )
        }
    }
}

@Composable
private fun YearMonthScrollableSelector(currentYearMonth: YearMonth, onYearMonthSelected: (YearMonth) -> Unit) {
    // 年份列表
    val YEARS = (2000..2099).toList()
    // 月份列表
    val MONTHS = (1..12).toList()

    // 选中的年份和月份，默认为当前年月
    var selectedYear by remember { mutableStateOf(currentYearMonth.year) }
    var selectedMonth by remember { mutableStateOf(currentYearMonth.monthValue) }

    Column {
        // Text("Selected Year: $selectedYear ")
        // Text("Selected Month: $selectedMonth ")
        Row {
            IntListPicker(
                YEARS,
                initialSelectedIndex = selectedYear - YEARS[0],
                onItemSelected = { _, year ->
                    selectedYear = year
                    onYearMonthSelected(YearMonth.of(selectedYear, selectedMonth))
                },
                showTitle = false,
                modifier = Modifier.width(100.dp)
            )
            VerticalDivider()
            IntListPicker(
                MONTHS,
                initialSelectedIndex = selectedMonth - 1,
                onItemSelected = { _, month ->
                    selectedMonth = month
                    onYearMonthSelected(YearMonth.of(selectedYear, selectedMonth))
                },
                showTitle = false,
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

@Composable
private fun DayOfWeekHeader(startFromSunday: Boolean) {
    val daysOfWeek = remember(startFromSunday) {
        if (startFromSunday) {
            DayOfWeek.entries.let { days ->
                listOf(days.last()) + days.dropLast(1)
            }
        } else {
            DayOfWeek.entries
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { dayOfWeek: DayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DaysGrid(
    yearMonth: YearMonth,
    dateRange: DateRange,
    startFromSunday: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = remember(yearMonth, startFromSunday) {
        generateDaysForMonth(yearMonth, startFromSunday)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier
            .fillMaxWidth()
            // 添加大小变化动画
            .animateContentSize()
    ) {
        items(days.size) { index ->
            val dateWithInfo = days[index]
            DayCell(
                date = dateWithInfo.date,
                isCurrentMonth = dateWithInfo.isCurrentMonth,
                isSelected = dateWithInfo.date == dateRange.startDate ||
                        dateWithInfo.date == dateRange.endDate,
                isInRange = dateRange.startDate != null && dateRange.endDate != null &&
                        dateWithInfo.date.isAfter(dateRange.startDate) &&
                        dateWithInfo.date.isBefore(dateRange.endDate),
                onDateSelected = onDateSelected,
                // 添加淡入淡出动画
                modifier = Modifier.animateContentSize()
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isInRange: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            // .padding(2.dp)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isInRange -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                else -> MaterialTheme.colorScheme.onSurface
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun generateDaysForMonth(
    yearMonth: YearMonth,
    startFromSunday: Boolean
): List<DateWithInfo> {
    // 计算当前月份的第一天和最后一天
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    // 计算当前月份第一天是星期几
    val firstDayOfWeek = if (startFromSunday) DayOfWeek.SUNDAY else DayOfWeek.MONDAY
    // 计算当前月份第一天在日历上的偏移量
    val daysBeforeStart = (firstDayOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7

    // Create the grid with null placeholders for empty cells
    return buildList {
        // 添加上个月的日期
        val previousMonth = yearMonth.minusMonths(1)
        val previousMonthLastDay = previousMonth.atEndOfMonth()
        for (i in daysBeforeStart downTo 1) {
            add(
                DateWithInfo(
                    date = previousMonthLastDay.minusDays((i - 1).toLong()),
                    isCurrentMonth = false
                )
            )
        }

        // 添加当前月的日期
        var currentDay = firstDayOfMonth
        while (currentDay <= lastDayOfMonth) {
            add(
                DateWithInfo(
                    date = currentDay,
                    isCurrentMonth = true
                )
            )
            currentDay = currentDay.plusDays(1)
        }

        // 添加下个月的日期
        val remainingDays = (7 - size % 7) % 7
        val nextMonth = yearMonth.plusMonths(1)
        var nextMonthDay = nextMonth.atDay(1)
        repeat(remainingDays) {
            add(
                DateWithInfo(
                    date = nextMonthDay,
                    isCurrentMonth = false
                )
            )
            nextMonthDay = nextMonthDay.plusDays(1)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateRangePickerDemo() {
    var selectedDateRange by remember { mutableStateOf(DateRange()) }

    Column {

        // 显示选中的日期范围
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Start Date: ${selectedDateRange.startDate}")
            Text("End Date: ${selectedDateRange.endDate}")
        }
        SimpleDateRangePicker(
            startFromSunday = false,
            onDateRangeSelected = { dateRange ->
                selectedDateRange = dateRange
            },
            modifier = Modifier.width(300.dp)
        )

    }
}


