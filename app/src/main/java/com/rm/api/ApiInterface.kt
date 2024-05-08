package com.rm.api

import com.rm.api.request.login.LoginRequest
import com.rm.api.request.project.AddProjectRequest
import com.rm.api.request.resource.AddResourceRequest
import com.rm.api.response.login.LoginResponse
import com.rm.api.response.masterdata.MasterDataResponse
import com.rm.api.response.project.AddProjectResponse
import com.rm.api.response.project.ProjectDetailsResponse
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.api.response.project.ProjectListResponse
import com.rm.api.response.project.ProjectNameResponse
import com.rm.api.response.resource.AddResourceResponse
import com.rm.api.response.resource.ResourceDetailResponse
import com.rm.api.response.resource.ResourceListResponse
import com.rm.utils.empty
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface ApiInterface {

    class RMEnvironment(
        private val baseUrl: String,
        private var urlPath: String,
        private val pathMap: Map<String, String>? = null
    ) : ReadWriteProperty<Any?, String> {

        private var completeUrl: String = String.empty()

        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            replacePathValues()
            return baseUrl + urlPath
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            replacePathValues()
            completeUrl = baseUrl + urlPath
        }

        private fun replacePathValues() {
            if (pathMap != null && pathMap.isNotEmpty()) {
                pathMap.keys.forEach { key ->
                    pathMap[key]?.let {
                        urlPath = urlPath.replace(key, it)
                    }
                }
            }
        }
    }

    companion object {

        private const val BASE_URL = "https://us-central1-res-management-torinit.cloudfunctions.net/"

        fun getProjectDetailUrl(projectId: String): String {
            val url by RMEnvironment(
                BASE_URL,
                "app/getProject/{p1}",
                mapOf("{p1}" to projectId)
            )
            return url
        }

        fun getResourceDetailUrl(resourceId: String): String {
            val url by RMEnvironment(
                BASE_URL,
                "app/getResource/{p1}",
                mapOf("{p1}" to resourceId)
            )
            return url
        }

        fun getEditResourceUrl(resourceId: String): String {
            val url by RMEnvironment(
                BASE_URL,
                "app/updateResource/{p1}",
                mapOf("{p1}" to resourceId)
            )
            return url
        }

        fun getDeleteResourceUrl(resourceId: String): String {
            val url by RMEnvironment(
                BASE_URL,
                "app/deleteResource/{p1}",
                mapOf("{p1}" to resourceId)
            )
            return url
        }

        fun getUpdateProjectUrl(projectId: String): String {
            val url by RMEnvironment(
                BASE_URL,
                "app/updateProject/{p1}",
                mapOf("{p1}" to projectId)
            )
            return url
        }

        fun getDeleteProjectUrl(projectId: String): String {
            val url by RMEnvironment(
                BASE_URL,
                "app/deleteProject/{p1}",
                mapOf("{p1}" to projectId)
            )
            return url
        }
    }

    @POST
    suspend fun login(
        @Url url: String = "app/login",
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET
    suspend fun getMasterData(
        @Url url: String = "app/getMasterData"
    ): Response<MasterDataResponse>

    @GET
    suspend fun getProjectName(
        @Url url: String = "app/getProjectsName"
    ): Response<ProjectNameResponse>

    @GET
    suspend fun getResources(
        @Url url: String = "app/getAllResources"
    ): Response<ResourceListResponse>

    @GET
    suspend fun getResourceDetail(
        @Url url: String
    ): Response<ResourceDetailResponse>

    @POST
    suspend fun addResource(
        @Url url: String = "app/addResource",
        @Body request: AddResourceRequest
    ): Response<AddResourceResponse>

    @PUT
    suspend fun editResource(
        @Url url: String,
        @Body request: AddResourceRequest
    ): Response<AddResourceResponse>

    @DELETE
    suspend fun deleteResource(
        @Url url: String,
    ): Response<Unit>

    /* Projects */
    @GET
    suspend fun getProjects(
        @Url url: String = "app/getAllProjects"
    ): Response<ProjectListResponse>

    @GET
    suspend fun getProjectDetail(
        @Url url: String
    ): Response<ProjectDetailsResponse>

    @POST
    suspend fun addProject(
        @Url url: String = "app/addProject",
        @Body request: AddProjectRequest
    ): Response<AddProjectResponse>

    @PUT
    suspend fun updateProject(
        @Url url: String,
        @Body request: ProjectDetailsResponseData
    ): Response<Unit>

    @DELETE
    suspend fun deleteProject(
        @Url url: String
    ): Response<Unit>
}