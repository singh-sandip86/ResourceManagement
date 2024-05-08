package com.rm.view.login.repository

import com.rm.api.response.login.LoginResponse
import com.rm.api.response.masterdata.MasterDataResponse
import com.rm.api.response.project.ProjectNameResponse
import retrofit2.Response

interface LoginRepository {
    suspend fun login(email: String, password: String): Response<LoginResponse>

    suspend fun getMasterData(): Response<MasterDataResponse>

    suspend fun getProjectName(): Response<ProjectNameResponse>
}