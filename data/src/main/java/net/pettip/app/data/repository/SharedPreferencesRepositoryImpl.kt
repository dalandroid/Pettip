package net.pettip.app.data.repository

import android.content.Context
import net.pettip.app.data.local.SharedPreferencesHelper
import net.pettip.app.domain.repository.SharedPreferencesRepository
import javax.inject.Inject

/**
 * @Project     : PetTip-Android
 * @FileName    : SharedPreferencesRepositoryImpl
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.data.repository
 * @see net.pettip.app.data.repository.SharedPreferencesRepositoryImpl
 */
class SharedPreferencesRepositoryImpl @Inject constructor(
    context: Context
): SharedPreferencesRepository {

    init {
        SharedPreferencesHelper.init(context)
    }

    override fun setAccessToken(accessToken: String?) {
        SharedPreferencesHelper.setAccessToken(accessToken)
    }

    override fun getAccessToken(): String? {
        return SharedPreferencesHelper.getAccessToken()
    }

    override fun setRefreshToken(refreshToken: String?) {
        SharedPreferencesHelper.setRefreshToken(refreshToken)
    }

    override fun getRefreshToken(): String? {
        return SharedPreferencesHelper.getRefreshToken()
    }

}