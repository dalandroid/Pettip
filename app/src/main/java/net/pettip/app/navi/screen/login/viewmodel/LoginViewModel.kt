package net.pettip.app.navi.screen.login.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pettip.app.domain.entity.login.LoginReq
import net.pettip.app.domain.repository.LoginRepository
import net.pettip.app.domain.usecase.LoginUseCase
import javax.inject.Inject

/**
 * @Project     : PetTip-Android
 * @FileName    : LoginViewModel
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.navi.viewmodel.login
 * @see net.pettip.app.navi.viewmodel.login.LoginViewModel
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _email = MutableStateFlow<String?>(null)

    private val _unqId = MutableStateFlow<String?>(null)

    private val _nickName = MutableStateFlow<String?>(null)

    private val appTypNm = Build.MODEL.toString()

    fun googleLogin(context: Context, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val loginResult = loginUseCase.googleLogin(context)
                if (loginResult != null) {
                    val loginReq = LoginReq(appTypNm, loginResult.email, loginResult.id)
                    loginUseCase.login(loginReq)
                } else {
                    -1 // 실패 시 반환할 값
                }
            }
            onResult(result)
        }
    }

    fun kakaoLogin(context: Context, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val loginResult = loginUseCase.kakaoLogin(context)
                if (loginResult != null) {
                    val loginReq = LoginReq(appTypNm, loginResult.email, loginResult.id)
                    loginUseCase.login(loginReq)
                } else {
                    -1 // 실패 시 반환할 값
                }
            }
            onResult(result)
        }
    }

    fun naverLogin(context: Context, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val loginResult = loginUseCase.naverLogin(context)
                if (loginResult != null) {
                    val loginReq = LoginReq(appTypNm, loginResult.email, loginResult.id)
                    loginUseCase.login(loginReq)
                } else {
                    -1 // 실패 시 반환할 값
                }
            }
            onResult(result)
        }
    }
}