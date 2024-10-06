package net.pettip.app.navi.screen.main.view

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.ReportDrawn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.launch
import net.pettip.app.navi.R
import kotlin.math.roundToInt

/**
 * @Project     : PetTip-Android
 * @FileName    : HomeScreen
 * @Date        : 2024-08-26
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen.main.view
 * @see net.pettip.app.navi.screen.main.view.HomeScreen
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun WalkScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    val systemUiController = rememberSystemUiController()

    val state = rememberLazyListState()

    val density = LocalDensity.current
    val maxHeight = 240.dp
    var offset by remember { mutableStateOf(maxHeight - 32.dp) }
    val topBarHeight = 40.dp + with(density) { WindowInsets.statusBars.asPaddingValues().calculateTopPadding() }
    val isTopBarVisible = offset > maxHeight - (maxHeight-topBarHeight)/2

    systemUiController.setStatusBarColor(
        color = if (isTopBarVisible) Color.Transparent else Color.White,
        darkIcons = !isTopBarVisible
    )

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                /** PreScroll : lazyColumn 에 동작이 전파 되기 전에 처리
                 * offset != topBarHeight : topBarHeight 가 아닌 경우 lazyColumn 에 스크롤 전파 X
                 * topBarHeight 에 도달한 경우 스크롤 전파
                 * */

                return with(density) {
                    if (offset != topBarHeight) {
                        offset = (offset + delta.toDp()).coerceIn(topBarHeight,maxHeight)
                        Offset(0f,delta)
                    }else{
                        Offset(0f,0f)
                    }
                }
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                /** postScroll : topBarHeight 에 도달한 이후 동작
                 *  state.canScrollBackward : lazyColumn 에 스크롤 전파
                 *  state 가 false 가 되면 offset 을 이동
                 * */

                return with(density) {
                    if (state.canScrollBackward){
                        Log.d("TAG", "onPostScroll: true")
                        Offset(0f,0f)
                    }else{
                        Log.d("TAG", "onPostScroll: false")
                        offset = (offset + delta.toDp()).coerceIn(topBarHeight,maxHeight)
                        Offset(0f,delta)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {

        systemUiController.setStatusBarColor(Color.Transparent)

        onDispose {
            systemUiController.setStatusBarColor(Color.White)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ){
        Row (
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 16.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "뒤로가기",
                tint = if (isTopBarVisible) Color.White else Color.Black
            )

            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "홈",
                tint = if (isTopBarVisible) Color.White else Color.Black
            )
        }// Topbar row

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(offset + 32.dp)
        ){
            AsyncImage(
                model = R.drawable.cat,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isTopBarVisible) {
                                listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                            }else{
                                listOf(Color.White, Color.White)
                            }
                        )
                    )
            )
        }// image box

        LazyColumn (
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offset.roundToPx()) }
                .graphicsLayer {
                    // Defer reading state until draw phase.
                    val cornerSize = 32.dp
                    shape = RoundedCornerShape(
                        topStart = cornerSize,
                        topEnd = cornerSize
                    )
                    clip = true
                }
                .background(Color.White)
        ){
            stickyHeader {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White)
                )
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Blue))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Blue))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Red))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Blue))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Blue))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Red))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Blue))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Blue))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Red))
            }
        }
    }
}
