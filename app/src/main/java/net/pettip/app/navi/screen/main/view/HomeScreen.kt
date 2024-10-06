package net.pettip.app.navi.screen.main.view

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.pettip.app.navi.component.CustomTabRow
import net.pettip.app.navi.component.CustomTabRow2
import kotlin.math.roundToInt

/**
 * @Project     : PetTip-Android
 * @FileName    : HomeScreen
 * @Date        : 2024-08-26
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen.main.view
 * @see net.pettip.app.navi.screen.main.view.HomeScreen
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    var selectedTabIndex by remember { mutableStateOf(0) }
    val list = listOf("Home", "Feed", "Profile", "Settings")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ){
        Box(modifier = Modifier.fillMaxWidth(0.8f)){
            CustomTabRow2(
                tabList = list,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { int -> selectedTabIndex = int },
                backGroundColor = Color.White,
                tabHeight = 36.dp,
                textSize = 16.sp,
                tabPadding = PaddingValues(horizontal = 8.dp)
            )
        }
    }
}
