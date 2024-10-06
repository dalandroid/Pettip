package net.pettip.app.navi.component

import android.util.Log
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @Project     : PetTip-Android
 * @FileName    : GlobalLoadingScreen
 * @Date        : 2024-05-01
 * @author      : CareBiz
 * @description : net.pettip.app.navi.componet
 * @see net.pettip.app.navi.componet.GlobalLoadingScreen
 */

object LoadingState {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun show() {
        _isLoading.value = true
    }

    fun hide() {
        _isLoading.value = false
    }
}

@Composable
fun GlobalLoadingScreen(){
    val isLoading = LoadingState.isLoading.collectAsState().value

    if (isLoading) {
        Dialog(
            onDismissRequest = { LoadingState.hide() },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            )
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = Color.White,
                trackColor = Color.Gray,
                strokeCap = StrokeCap.Butt
            )
        }
    }
}