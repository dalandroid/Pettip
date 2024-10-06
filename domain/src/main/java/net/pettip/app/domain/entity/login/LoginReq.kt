package net.pettip.app.domain.entity.login

import com.google.gson.annotations.SerializedName

data class LoginReq(
    @SerializedName("appTypNm")
    var appTypNm: String?,
    @SerializedName("userID")
    var userID: String?,
    @SerializedName("userPW")
    var userPW: String?,
)