package net.pettip.app.domain.utils

import org.json.JSONObject

/**
 * @Project     : PetTip-Android
 * @FileName    : ErrorBodyParse
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.domain.utils
 * @see net.pettip.app.domain.utils.ErrorBodyParse
 */
fun errorBodyParseStatus(errorBodyString: String?):String{
    if (errorBodyString != null) {
        // errorBodyString은 JSON 형식으로 보이므로 파싱하여 detailMessage를 얻을 수 있습니다.
        val json = JSONObject(errorBodyString)
        val statusCode = json.optString("statusCode")

        return if (statusCode.isNotEmpty()) {
            statusCode
        } else {
            "통신에 실패했습니다"
        }
    }

    return "통신에 실패했습니다"
}