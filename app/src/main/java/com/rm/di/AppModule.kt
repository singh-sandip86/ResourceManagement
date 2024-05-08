package com.rm.di

import com.rm.api.ApiInterface
import com.rm.view.login.repository.LoginRepository
import com.rm.view.login.repository.LoginRepositoryImpl
import com.rm.view.project.repository.ProjectRepository
import com.rm.view.project.repository.ProjectRepositoryImpl
import com.rm.view.resource.repository.ResourceRepository
import com.rm.view.resource.repository.ResourceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideLoginRepository(apiInterface: ApiInterface): LoginRepository =
        LoginRepositoryImpl(apiInterface)

    @Singleton
    @Provides
    fun provideResourceRepository(apiInterface: ApiInterface): ResourceRepository =
        ResourceRepositoryImpl(apiInterface)

    @Singleton
    @Provides
    fun provideProjectRepository(apiInterface: ApiInterface): ProjectRepository =
        ProjectRepositoryImpl(apiInterface)
}