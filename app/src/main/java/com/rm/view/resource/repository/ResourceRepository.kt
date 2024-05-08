package com.rm.view.resource.repository

import com.rm.api.request.resource.AddResourceRequest
import com.rm.api.response.resource.AddResourceResponse
import com.rm.api.response.resource.ResourceDetailResponse
import com.rm.api.response.resource.ResourceListResponse
import retrofit2.Response

interface ResourceRepository {
    suspend fun getResources(): Response<ResourceListResponse>

    suspend fun getResourceDetail(resourceId: String): Response<ResourceDetailResponse>

    suspend fun addResource(
        request: AddResourceRequest
    ): Response<AddResourceResponse>

    suspend fun editResource(
        resourceId: String,
        request: AddResourceRequest
    ): Response<AddResourceResponse>

    suspend fun deleteResource(resourceId: String): Response<Unit>
}