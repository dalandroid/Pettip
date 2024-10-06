package net.pettip.app.domain.entity.login

import com.google.gson.annotations.SerializedName

/**
 * @Project     : PetTip-Android
 * @FileName    : LoginRes
 * @Date        : 2024-05-01
 * @author      : CareBiz
 * @description : net.pettip.app.navi.datamodel.login
 * @see net.pettip.app.navi.datamodel.login.LoginRes
 */
data class LoginRes(
    @SerializedName("data")
    var data: LoginResData?,
    @SerializedName("detailMessage")
    var detailMessage: String?, // null
    @SerializedName("resultMessage")
    var resultMessage: String?, // null
    @SerializedName("statusCode")
    var statusCode: Int // 200
)

data class LoginResData(
    @SerializedName("accessToken")
    var accessToken: String,
    @SerializedName("failReason")
    var failReason: String?, // null
    @SerializedName("message")
    var message: String?, // null
    @SerializedName("refreshToken")
    var refreshToken: String?,
    @SerializedName("scope")
    var scope: String?, // profile email
    @SerializedName("status")
    var status: Boolean?, // true
    @SerializedName("tokenType")
    var tokenType: String?, // Bearer
    @SerializedName("userId")
    var userId: String?, //
    @SerializedName("nckNm")
    var nckNm: String?, //
    @SerializedName("email")
    var email: String?, //
    @SerializedName("appKeyVl")
    var appKeyVl: String?,
)