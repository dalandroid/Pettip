package net.pettip.app.navi.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @Project     : PetTip-Android
 * @FileName    : CustomTabRow
 * @Date        : 2024-08-27
 * @author      : CareBiz
 * @description : net.pettip.app.navi.component
 * @see net.pettip.app.navi.component.CustomTabRow
 */
@Composable
fun CustomTabRow(
    tabList: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabHeight: Dp = 24.dp,
    textSize: TextUnit = 14.sp,
    tabPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
    backGroundColor: Color = Color.White
){

    val tabXPositions = remember { MutableList(tabList.size) { 8.dp } }
    var selectedTabWidth by remember { mutableStateOf(0.dp) }

    val animatedXPosition by animateDpAsState(label = "", targetValue = tabXPositions[selectedTabIndex])

    SubcomposeLayout(
        modifier = Modifier
            .background(backGroundColor, RoundedCornerShape(tabHeight/2+8.dp))
    ) {constraints ->
        val itemPlaceable = subcompose("tabRow"){
            tabList.forEachIndexed {index, it ->
                Box(
                    modifier = Modifier
                        .height(tabHeight)
                        .clip(RoundedCornerShape(tabHeight / 2))
                        .clickable {

                            onTabSelected(index)
                        }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = it, color = Color(0xFFA374DB), fontSize = textSize)
                }
            }
        }.map { it.measure(constraints) }

        val indicatorPlaceable = subcompose("indicator") {
            Box(
                modifier = Modifier
                    .height(tabHeight)
                    .width(itemPlaceable[selectedTabIndex].width.toDp())
                    .clip(RoundedCornerShape(tabHeight / 2))
                    .background(Color(0xFFA374DB))
            )
        }[0].measure(constraints)

        layout(
            width = itemPlaceable.sumOf { it.width } + (itemPlaceable.size-1) * 8.dp.roundToPx() + 16.dp.roundToPx(),
            height = itemPlaceable.maxOf { it.height } + 8.dp.roundToPx()
        ){
            var xPosition = 8.dp.roundToPx()

            tabXPositions.clear()
            // Place each tab
            itemPlaceable.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = xPosition, y = 4.dp.roundToPx())
                tabXPositions.add(xPosition.toDp())

                if (index == selectedTabIndex) {
                    selectedTabWidth = placeable.width.toDp()
                }

                xPosition += placeable.width + 8.dp.roundToPx()
            }

            indicatorPlaceable.placeRelative(
                x = animatedXPosition.roundToPx(),
                y = 4.dp.roundToPx()
            )
        }
    }
}