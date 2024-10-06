package net.pettip.app.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * @Project     : PetTip-Android
 * @FileName    : SharedPreferencesHelper
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.data.local
 * @see net.pettip.app.data.local.SharedPreferencesHelper
 */
object SharedPreferencesHelper {
    private const val DATA = "data"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE)
    }

    fun setAccessToken(accessToken: String?) {
        sharedPreferences.edit().putString("AccessToken", accessToken).apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("AccessToken", null)
    }

    fun setRefreshToken(refreshToken: String?) {
        sharedPreferences.edit().putString("RefreshToken", refreshToken).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("RefreshToken", null)
    }
}