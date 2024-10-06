package net.pettip.app.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.pettip.app.data.network.ApiService
import net.pettip.app.data.repository.LoginRepositoryImpl
import net.pettip.app.data.repository.SharedPreferencesRepositoryImpl
import net.pettip.app.domain.repository.LoginRepository
import net.pettip.app.domain.repository.SharedPreferencesRepository
import javax.inject.Singleton

/**
 * @Project     : PetTip-Android
 * @FileName    : RepositoryModule
 * @Date        : 2024-08-06
 * @author      : CareBiz
 * @description : net.pettip.app.data.di
 * @see net.pettip.app.data.di.RepositoryModule
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSharedPreferencesRepository(@ApplicationContext context: Context): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(apiService: ApiService):LoginRepository{
        return LoginRepositoryImpl(apiService)
    }
}