package net.pettip.app.navi.screen.login.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import net.pettip.app.navi.component.LoadingState
import net.pettip.app.navi.screen.login.viewmodel.LoginViewModel

/**
 * @Project     : PetTip-Android
 * @FileName    : LoginScreen
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen.login
 * @see net.pettip.app.navi.screen.login.LoginScreen
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navMain:()->Unit = {}
){
    Log.d("LOG",viewModel.hashCode().toString())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    /**
     * onResult : 0 로그인 성공, 1 로그인 실패, 2 통신 실패, 3 403(차단 사용자)
     * */

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeContentPadding()
    ){
        Button(
            onClick = {
                LoadingState.show()
                viewModel.kakaoLogin(context){result ->
                    when(result){
                        0 -> navMain()
                        1 -> Log.d("RESULT","로그인 실패")
                        2 -> Log.d("RESULT","통신 실패")
                        3 -> Log.d("RESULT","차단된 사용자")
                        -1 -> Log.d("RESULT","catch")
                    }
                    LoadingState.hide()
                }
            }
        ) {
            Text(text = "Kakao")
        }

        Button(
            onClick = {
                LoadingState.show()
                viewModel.naverLogin(context){result ->
                    when(result){
                        0 -> navMain()
                        1 -> Log.d("RESULT","로그인 실패")
                        2 -> Log.d("RESULT","통신 실패")
                        3 -> Log.d("RESULT","차단된 사용자")
                        -1 -> Log.d("RESULT","catch")
                    }
                    LoadingState.hide()
                }
            }
        ) {
            Text(text = "Naver")
        }

        Button(
            onClick = {
                LoadingState.show()
                viewModel.googleLogin(context){result ->
                    when(result){
                        0 -> navMain()
                        1 -> Log.d("RESULT","로그인 실패")
                        2 -> Log.d("RESULT","통신 실패")
                        3 -> Log.d("RESULT","차단된 사용자")
                        -1 -> Log.d("RESULT","catch")
                    }
                    LoadingState.hide()
                }
            }
        ) {
            Text(text = "Google")
        }
    }
}