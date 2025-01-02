package net.pettip.app.navi.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * @Project     : PetTip-Android
 * @FileName    : CustomTabRow2
 * @Date        : 2024-08-28
 * @author      : CareBiz
 * @description : net.pettip.app.navi.component
 * @see net.pettip.app.navi.component.CustomTabRow2
 */
@Composable
fun CustomTabRow2(
    tabList: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabHeight: Dp = 24.dp,
    rowPadding: Dp = 8.dp,
    textSize: TextUnit = 14.sp,
    tabPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
    backGroundColor: Color = Color.White,
    color: Color = Color(0xFFA374DB)
){
    val density = LocalDensity.current

    val xPositions = remember { MutableList(tabList.size) { IntOffset(0,0) } }
    val tabSize = remember { MutableList(tabList.size){ 0.dp } }

    var isInitialized by remember { mutableStateOf(false) }

    val animatedXPosition by animateIntOffsetAsState(targetValue = xPositions[selectedTabIndex], label = "")
    val animatedTabSize by animateDpAsState(targetValue = tabSize[selectedTabIndex], label = "")
    val alphaValue by animateFloatAsState(targetValue = if (isInitialized) 1f else 0f, animationSpec = tween(durationMillis = 500), label = "")

    Box (
        modifier = Modifier
            .height(tabHeight + rowPadding)
            .fillMaxWidth()
            .clip(RoundedCornerShape((tabHeight + rowPadding) / 2))
            .background(backGroundColor)
            .padding(tabPadding),
    ){
        if (isInitialized){
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = animatedXPosition.x,
                            y = animatedXPosition.y// Set Y offset to 0
                        )
                    }
                    .width(animatedTabSize)
                    .height(tabHeight)
                    .clip(RoundedCornerShape(tabHeight / 2))
                    .background(color.copy(alpha = alphaValue.coerceIn(0f,1f)))
            )
        }

        Row (
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            tabList.forEachIndexed {index, it ->
                Box(modifier = Modifier
                    .height(tabHeight)
                    .wrapContentWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        val offset = layoutCoordinates.positionInParent()
                        xPositions[index] = IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                        val widthDp = with(density) { layoutCoordinates.size.width.toDp() }
                        tabSize[index] = widthDp

                        if (!isInitialized && index == selectedTabIndex) {
                            isInitialized = true
                        }
                    }
                    .clip(RoundedCornerShape(tabHeight / 2))
                    .clickable {
                        onTabSelected(index)
                    },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = it,
                        color = if (index == selectedTabIndex) Color.White else color,
                        fontSize = textSize,
                        modifier = Modifier
                            .padding(tabPadding)
                    )
                }
            }
        }
    }
}