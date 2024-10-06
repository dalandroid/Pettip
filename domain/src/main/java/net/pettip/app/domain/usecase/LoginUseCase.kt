package net.pettip.app.domain.usecase

import android.content.Context
import net.pettip.app.domain.entity.login.LoginReq
import net.pettip.app.domain.entity.login.User
import net.pettip.app.domain.repository.LoginRepository
import javax.inject.Inject

/**
 * @Project     : PetTip-Android
 * @FileName    : LoginUseCase
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.domain.usecase
 * @see net.pettip.app.domain.usecase.LoginUseCase
 */
class LoginUseCase @Inject constructor (
    private val loginRepository:LoginRepository
){
    suspend fun googleLogin(context: Context): User? {
        return loginRepository.googleLogin(context)
    }

    suspend fun kakaoLogin(context: Context): User? {
        return loginRepository.kakaoLogin(context)
    }

    suspend fun naverLogin(context: Context): User? {
        return loginRepository.naverLogin(context)
    }

    suspend fun login(loginReq: LoginReq):Int{
        return loginRepository.login(loginReq)
    }
}