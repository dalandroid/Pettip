package net.pettip.app.navi.screen.main.view

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import androidx.activity.compose.ReportDrawn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.random.Random

/**
 * @Project     : PetTip-Android
 * @FileName    : HomeScreen
 * @Date        : 2024-08-26
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen.main.view
 * @see net.pettip.app.navi.screen.main.view.HomeScreen
 */
@Composable
fun TipScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        DragAndDropBox()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragAndDropBox(modifier: Modifier = Modifier){
    Column (
        modifier = modifier
            .fillMaxSize()
    ){

        var list = remember { List(4){ i -> "Drag Me! $i"}.toMutableStateList() }
        var recomposition by remember{ mutableStateOf(false) }
        val colors = remember {
            (1..list.size).map {
                Color(Random.nextLong()).copy(alpha = 1f)
            }
        }

        repeat(list.size){index ->
            key(recomposition) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(colors[index])
                        .clickable { Log.d("DragAndDrop",index.toString()) }
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        val draggedData = event.toAndroidDragEvent().clipData.getItemAt(0).text
                                        val temp = list[index]
                                        list[index] = list[draggedData.toString().toInt()]
                                        list[draggedData.toString().toInt()] = temp
                                        recomposition = !recomposition

                                        return true
                                    }

                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = list[index],
                        fontSize = 40.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .dragAndDropSource {
                                detectTapGestures(
                                    onLongPress = {
                                        startTransfer(
                                            DragAndDropTransferData(
                                                clipData = ClipData.newPlainText(
                                                    "index",
                                                    index.toString()
                                                )
                                            )
                                        )
                                    }
                                )
                            }
                    )
                }
            }
        }
    }
}
