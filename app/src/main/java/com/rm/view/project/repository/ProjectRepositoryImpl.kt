package com.rm.view.project.repository

import com.rm.api.ApiInterface
import com.rm.api.request.project.AddProjectRequest
import com.rm.api.response.project.AddProjectResponse
import com.rm.api.response.project.ProjectDetailsResponse
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.api.response.project.ProjectListResponse
import retrofit2.Response
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface
) : ProjectRepository {

    override suspend fun getProjects(): Response<ProjectListResponse> {
        return apiInterface.getProjects()
    }

    override suspend fun getProjectDetails(projectId: String): Response<ProjectDetailsResponse> {
        return apiInterface.getProjectDetail(
            url = ApiInterface.getProjectDetailUrl(projectId = projectId)
        )
    }

    override suspend fun addProject(request: AddProjectRequest): Response<AddProjectResponse> {
        return apiInterface.addProject(
            request = request
        )
    }

    override suspend fun updateProject(
        projectId: String,
        request: ProjectDetailsResponseData
    ): Response<Unit> {
        return apiInterface.updateProject(
            url = ApiInterface.getUpdateProjectUrl(projectId = projectId),
            request = request
        )
    }

    override suspend fun deleteProject(projectId: String): Response<Unit> {
        return apiInterface.deleteProject(
            url = ApiInterface.getDeleteProjectUrl(projectId = projectId)
        )
    }
}