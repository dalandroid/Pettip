package net.pettip.app.data.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.pettip.app.data.network.ApiService
import net.pettip.app.data.network.RefreshRes
import net.pettip.app.data.network.TokenInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @Project     : PetTip-Android
 * @FileName    : NetworkModule
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.data.di
 * @see net.pettip.app.data.di.NetworkModule
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://pettip.net/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 로깅 레벨 설정
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


}