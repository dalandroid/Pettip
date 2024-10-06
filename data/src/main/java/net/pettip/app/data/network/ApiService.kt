package net.pettip.app.data.network

import net.pettip.app.domain.entity.login.LoginReq
import net.pettip.app.domain.entity.login.LoginRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @Project     : PetTip-Android
 * @FileName    : ApiService
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.data.network
 * @see net.pettip.app.data.network.ApiService
 */
interface ApiService {
    @POST("api/v1/member/login")
    fun login(@Body data: LoginReq): Call<LoginRes>
}