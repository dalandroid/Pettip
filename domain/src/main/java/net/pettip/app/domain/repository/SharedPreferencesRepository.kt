package net.pettip.app.domain.repository

/**
 * @Project     : PetTip-Android
 * @FileName    : SharedPreferencesRepository
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.domain.repository
 * @see net.pettip.app.domain.repository.SharedPreferencesRepository
 */
interface SharedPreferencesRepository {
    fun setAccessToken(accessToken: String?)
    fun getAccessToken(): String?
    fun setRefreshToken(refreshToken: String?)
    fun getRefreshToken(): String?
}