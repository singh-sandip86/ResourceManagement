package com.rm.view.project.repository

import com.rm.api.request.project.AddProjectRequest
import com.rm.api.response.project.AddProjectResponse
import com.rm.api.response.project.ProjectDetailsResponse
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.api.response.project.ProjectListResponse
import retrofit2.Response

interface ProjectRepository {

    suspend fun getProjects(): Response<ProjectListResponse>

    suspend fun getProjectDetails(projectId: String): Response<ProjectDetailsResponse>

    suspend fun addProject(request: AddProjectRequest): Response<AddProjectResponse>

    suspend fun updateProject(
        projectId: String,
        request: ProjectDetailsResponseData
    ): Response<Unit>

    suspend fun deleteProject(projectId: String): Response<Unit>
}