package net.pettip.app.navi.screen.main.view

import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

/**
 * @Project     : PetTip-Android
 * @FileName    : HomeScreen
 * @Date        : 2024-08-26
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen.main.view
 * @see net.pettip.app.navi.screen.main.view.HomeScreen
 */
@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "MY")
    }
}