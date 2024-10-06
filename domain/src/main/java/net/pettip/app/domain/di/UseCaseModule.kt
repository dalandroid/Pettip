package net.pettip.app.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.pettip.app.domain.repository.LoginRepository
import net.pettip.app.domain.usecase.LoginUseCase
import javax.inject.Singleton

/**
 * @Project     : PetTip-Android
 * @FileName    : UseCaseModule
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.domain.di
 * @see net.pettip.app.domain.di.UseCaseModule
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        loginRepository: LoginRepository
    ): LoginUseCase = LoginUseCase(loginRepository)


}