package net.pettip.app.navi.screen.login.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * @Project     : PetTip-Android
 * @FileName    : IntroScreen
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen.login
 * @see net.pettip.app.navi.screen.login.IntroScreen
 */
@Composable
fun IntroScreen(onNext: () -> Unit) {

    LaunchedEffect (Unit){
        delay(1500)
        onNext()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(text = "intro")
    }
}