package net.pettip.app.domain.repository

import android.content.Context
import net.pettip.app.domain.entity.login.LoginReq
import net.pettip.app.domain.entity.login.LoginRes
import net.pettip.app.domain.entity.login.User

/**
 * @Project     : PetTip-Android
 * @FileName    : LoginRepository
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.domain.repository
 * @see net.pettip.app.domain.repository.LoginRepository
 */
interface LoginRepository {
    suspend fun googleLogin(context: Context): User?
    suspend fun kakaoLogin(context: Context): User?
    suspend fun naverLogin(context: Context): User?
    suspend fun login(loginReq: LoginReq):Int
}