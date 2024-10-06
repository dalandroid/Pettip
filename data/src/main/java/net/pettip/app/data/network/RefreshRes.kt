package net.pettip.app.data.network

import com.google.gson.annotations.SerializedName

/**
 * @Project     : PetTip-Android
 * @FileName    : RefreshRes
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.data.network
 * @see net.pettip.app.data.network.RefreshRes
 */
data class RefreshRes(
    @SerializedName("data")
    var data: TokenData?,
    @SerializedName("detailMessage")
    var detailMessage: String, // null
    @SerializedName("resultMessage")
    var resultMessage: String, // null
    @SerializedName("statusCode")
    var statusCode: Int // 200
)

data class TokenData(
    @SerializedName("accessToken")
    var accessToken: String?,
    @SerializedName("failReason")
    var failReason: String?, // null
    @SerializedName("message")
    var message: String?, // null
    @SerializedName("refreshToken")
    var refreshToken: String?,
    @SerializedName("status")
    var status: Boolean, // true
    @SerializedName("userId")
    var userId: String?, //
    @SerializedName("nckNm")
    var nckNm: String?, //
    @SerializedName("email")
    var email: String?, //
    @SerializedName("appKeyVl")
    var appKeyVl: String?, //
)