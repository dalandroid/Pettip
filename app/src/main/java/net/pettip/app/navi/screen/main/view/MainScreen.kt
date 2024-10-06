package net.pettip.app.navi.screen.main.view

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * @Project     : PetTip-Android
 * @FileName    : MainScreen
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen.main
 * @see net.pettip.app.navi.screen.main.MainScreen
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    var currentTab by remember{ mutableStateOf(Tab.HOME) }

    BackOnPressed()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        bottomBar = {
            CustomBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    when (currentTab) {
                        Tab.HOME -> Modifier.padding(paddingValues) // BottomBar 위에 그려짐
                        Tab.WALK -> Modifier // BottomBar 위에 그려짐
                        Tab.TIP -> Modifier.padding(paddingValues) // BottomBar 뒤에 그려짐
                        Tab.MY -> Modifier.padding(paddingValues) // BottomBar 위에 그려짐
                    }
                )
        ) {
            when (currentTab) {
                Tab.HOME -> HomeScreen(navController = navController)
                Tab.WALK -> WalkScreen(navController = navController)
                Tab.TIP -> TipScreen(navController = navController) // BottomBar 뒤에 그려짐
                Tab.MY -> MyScreen(navController = navController)
            }
        }
    }
}

enum class Tab {
    HOME, WALK, TIP, MY
}

@Composable
fun CustomBottomBar(
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit,
    paddingValues: PaddingValues = PaddingValues(8.dp)
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
            .selectableGroup()
            .shadow(elevation = 10.dp)
            .background(Color.White)
    ){
        CustomBottomBarItem(
            isSelected = currentTab == Tab.HOME,
            onClick = { onTabSelected(Tab.HOME) }
        ){

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
                
                Text(text = "HOME")
            }
        }

        CustomBottomBarItem(
            isSelected = currentTab == Tab.WALK,
            onClick = { onTabSelected(Tab.WALK) }
        ){
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(imageVector = Icons.Default.Face, contentDescription = "Walk")

                Text(text = "WALK")
            }
        }

        CustomBottomBarItem(
            isSelected = currentTab == Tab.TIP,
            onClick = { onTabSelected(Tab.TIP) }
        ){
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Tip")

                Text(text = "TIP")
            }
        }

        CustomBottomBarItem(
            isSelected = currentTab == Tab.MY,
            onClick = { onTabSelected(Tab.MY) }
        ){
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "My"
                )

                Text(text = "MY")
            }
        }
    }
}

@Composable
fun RowScope.CustomBottomBarItem(
    isSelected:Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content:@Composable () -> Unit
){
    val ripple = rememberRipple(bounded = false)
    val contentColor = if (isSelected) Color.Blue else Color.Gray

    Box(
        modifier = Modifier
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = ripple
            )
            .weight(1f),
        contentAlignment = Alignment.Center
    ){
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Surface (
                color = Color.Transparent,
                contentColor = contentColor
            ){
                content()
            }
        }
    }
}


@Composable
fun BackOnPressed(
) {
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(true) }
    var backPressedTime = 0L
    val closeCmt = "한 번 더 누르면 앱이 종료됩니다"

    BackHandler(enabled = backPressedState) {
        if(System.currentTimeMillis() - backPressedTime <= 1000L) {
            // 앱 종료
            (context as Activity).finish()
        } else {
            backPressedState = true
            Toast.makeText(context, closeCmt, Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}