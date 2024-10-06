package net.pettip.app.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.pettip.app.data.network.ApiService
import net.pettip.app.domain.entity.login.LoginReq
import net.pettip.app.domain.entity.login.LoginRes
import net.pettip.app.domain.entity.login.User
import net.pettip.app.domain.repository.LoginRepository
import net.pettip.app.domain.utils.errorBodyParseStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.security.SecureRandom
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * @Project     : PetTip-Android
 * @FileName    : LoginRepositoryImpl
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.data.repository
 * @see net.pettip.app.data.repository.LoginRepositoryImpl
 */
class LoginRepositoryImpl @Inject constructor(
    private val apiService: ApiService
):LoginRepository {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun generateNonce(size: Int = 32): String {
        val random = SecureRandom()
        val nonce = ByteArray(size)
        random.nextBytes(nonce)
        return BigInteger(1, nonce).toString(16)
    }

    override suspend fun googleLogin(context: Context): User? {
        return suspendCancellableCoroutine { continuation ->
            coroutineScope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)

                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(true)
                        .setServerClientId("985887161836-gj9pqql898d85483bc1ik53a5t1kg6du.apps.googleusercontent.com")
                        .setAutoSelectEnabled(true)
                        .setNonce(generateNonce())
                        .build()

                    val request: androidx.credentials.GetCredentialRequest = androidx.credentials.GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = try {
                        credentialManager.getCredential(context, request)
                    } catch (e: Exception) {
                        Log.e("GoogleLogin", "Error during getCredential", e)
                        continuation.resume(null)
                        return@launch
                    }

                    when (val credential = result.credential) {
                        is CustomCredential -> {
                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                try {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                                    val idToken = googleIdTokenCredential.idToken
                                    val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
                                        .setAudience(listOf("985887161836-gj9pqql898d85483bc1ik53a5t1kg6du.apps.googleusercontent.com"))
                                        .build()

                                    val googleIdToken = withContext(Dispatchers.IO) {
                                        verifier.verify(idToken)
                                    }
                                    if (googleIdToken != null) {
                                        val email = googleIdToken.payload.email
                                        val unqId = googleIdToken.payload.subject
                                        val nickName = googleIdTokenCredential.displayName

                                        continuation.resume(User(unqId, email, nickName))
                                    } else {
                                        continuation.resume(null)
                                    }
                                } catch (e: GoogleIdTokenParsingException) {
                                    continuation.resume(null)
                                }
                            } else {
                                continuation.resume(null)
                            }
                        }
                        else -> {
                            continuation.resume(null)
                        }
                    }
                } catch (e: Exception) {
                    continuation.resume(null)
                }
            }
        }
    }



    override suspend fun kakaoLogin(context: Context): User? {
        return suspendCancellableCoroutine { continuation ->
            coroutineScope.launch {
                try {
                    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                        if (error != null) {
                            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                            continuation.resume(null)
                        } else if (token != null) {
                            Log.i("KAKAO", "카카오계정으로 로그인 성공 ${token.accessToken}")

                            UserApiClient.instance.me { user, error ->
                                if (user != null) {
                                    val email = user.kakaoAccount?.email ?: ""
                                    val id = user.id.toString()
                                    val nickname = user.kakaoAccount?.profile?.nickname ?: ""

                                    Log.d("KAKAO","email:${email},id:${id},nick:${nickname}")

                                    continuation.resume(User(id,email,nickname))
                                }
                            }
                        }
                    }

                    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                            if (error != null) {
                                Log.e("KAKAO", "카카오톡으로 로그인 실패", error)

                                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                    return@loginWithKakaoTalk
                                }

                                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                            } else if (token != null) {
                                Log.i("KAKAO", "카카오톡으로 로그인 성공 ${token.accessToken}")

                                UserApiClient.instance.me { user, error ->
                                    if (user != null) {
                                        val email = user.kakaoAccount?.email ?: ""
                                        val id = user.id.toString()
                                        val nickname = user.kakaoAccount?.profile?.nickname ?: ""

                                        continuation.resume(User(id,email,nickname))
                                    }
                                }
                            }
                        }
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    }
                }catch (e:Exception){
                    continuation.resume(null)
                }
            }
        }
    }

    override suspend fun naverLogin(context: Context): User? {
        return suspendCancellableCoroutine { continuation ->
            val oAuthLoginCallback = object : OAuthLoginCallback {
                override fun onError(errorCode: Int, message: String) {
                    Log.d("NAVER","error:${message}")
                    continuation.resume(null)
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    Log.d("NAVER","failure:${message}")
                    continuation.resume(null)
                }

                override fun onSuccess() {
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onError(errorCode: Int, message: String) {
                            Log.d("NAVER","suc -> error : ${message}")
                            continuation.resume(null)
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            Log.d("NAVER","suc -> failure : ${message}")
                            continuation.resume(null)
                        }

                        override fun onSuccess(result: NidProfileResponse) {
                            val email = result.profile?.email?: ""
                            val id = result.profile?.id?: ""
                            val nickname = result.profile?.nickname?: ""

                            continuation.resume(User(id,email,nickname))
                        }

                    })
                }
            }

            NaverIdLoginSDK.authenticate(context,oAuthLoginCallback)
        }
    }

    override suspend fun login(loginReq: LoginReq): Int {
        return suspendCancellableCoroutine { continuation ->
            coroutineScope.launch {
                try {
                    val call = apiService.login(loginReq)
                    call.enqueue(object : Callback<LoginRes> {
                        override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                            if (response.isSuccessful){
                                val body = response.body()
                                body?.let {
                                    if (body.statusCode == 200){
                                        continuation.resume(0)
                                    }else {
                                        continuation.resume(1)
                                    }
                                }
                            }else{
                                val errorBodyString = response.errorBody()!!.string()
                                val status = errorBodyParseStatus(errorBodyString)

                                if (status == "403"){
                                    Log.d("LOG","차단")
                                    continuation.resume(3)
                                }else{
                                    Log.d("LOG","로그인 실패")
                                    continuation.resume(1)
                                }
                            }
                        }

                        override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                            Log.d("LOG","통신실패시 진입")
                            continuation.resume(2)
                        }

                    })
                }catch (e: Exception){
                    continuation.resume(-1)
                }
            }
        }
    }

}