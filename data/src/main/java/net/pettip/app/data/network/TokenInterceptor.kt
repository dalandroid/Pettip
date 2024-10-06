package net.pettip.app.data.network

import com.google.gson.Gson
import net.pettip.app.domain.repository.SharedPreferencesRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * @Project     : PetTip-Android
 * @FileName    : TokenInterceptor
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.data.network
 * @see net.pettip.app.data.network.TokenInterceptor
 */
class TokenInterceptor @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 원래의 요청을 가져옵니다.
        val originalRequest = chain.request()

        val token: String? = sharedPreferencesRepository.getAccessToken()
        val refreshToken: String? = sharedPreferencesRepository.getRefreshToken()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            // AccessToken이 있는 경우 Authorization 헤더를 추가
            requestBuilder.header("Authorization", token)
        } else {
            requestBuilder.header("Authorization", sharedPreferencesRepository.getAccessToken() ?: "")
        }
        if (!refreshToken.isNullOrBlank()) {
            // RefreshToken이 있는 경우 Refresh 헤더를 추가
            requestBuilder.header("Refresh", refreshToken)
        } else {
            requestBuilder.header("Refresh", sharedPreferencesRepository.getRefreshToken() ?: "")
        }

        val requestWithToken = requestBuilder.build()
        val response = chain.proceed(requestWithToken)

        if (response.code == 403) {
            // G.dupleLogin = true
        } else if (response.code == 401) {
            // code 401 : access Token 만료시 응답 코드
            // response로 새로운 access Token과 Refresh가 떨어짐
            // 새로 받은 토큰으로 통신 재시도
            val gson = Gson()
            val responseBodyString = response.body?.string() ?: ""
            val tokenResponse = gson.fromJson(responseBodyString, RefreshRes::class.java)

            sharedPreferencesRepository.setAccessToken(tokenResponse.data?.accessToken)
            sharedPreferencesRepository.setRefreshToken(tokenResponse.data?.refreshToken)

            if (!sharedPreferencesRepository.getAccessToken().isNullOrBlank()) {
                val retryRequestBuilder = originalRequest.newBuilder()
                retryRequestBuilder.header("Authorization", sharedPreferencesRepository.getAccessToken() ?: "")
                retryRequestBuilder.header("Refresh", sharedPreferencesRepository.getRefreshToken() ?: "")

                val retryRequestWithToken = retryRequestBuilder.build()
                return chain.proceed(retryRequestWithToken)
            }
        }

        return response
    }
}