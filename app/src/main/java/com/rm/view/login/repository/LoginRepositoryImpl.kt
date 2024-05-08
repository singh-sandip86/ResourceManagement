package com.rm.view.login.repository

import com.rm.api.ApiInterface
import com.rm.api.request.login.LoginRequest
import com.rm.api.response.login.LoginResponse
import com.rm.api.response.masterdata.MasterDataResponse
import com.rm.api.response.project.ProjectNameResponse
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val apiInterface: ApiInterface) :
    LoginRepository {
    override suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiInterface.login(
            loginRequest = LoginRequest(
                username = email,
                password = password
            )
        )
    }

    override suspend fun getMasterData(): Response<MasterDataResponse> {
        return apiInterface.getMasterData()
    }

    override suspend fun getProjectName(): Response<ProjectNameResponse> {
        return apiInterface.getProjectName()
    }
}