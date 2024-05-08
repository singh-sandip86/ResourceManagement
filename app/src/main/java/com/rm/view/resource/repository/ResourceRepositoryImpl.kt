package com.rm.view.resource.repository

import com.rm.api.ApiInterface
import com.rm.api.request.resource.AddResourceRequest
import com.rm.api.response.resource.AddResourceResponse
import com.rm.api.response.resource.ResourceDetailResponse
import com.rm.api.response.resource.ResourceListResponse
import retrofit2.Response
import javax.inject.Inject

class ResourceRepositoryImpl @Inject constructor(private val apiInterface: ApiInterface) :
    ResourceRepository {

    override suspend fun getResources(): Response<ResourceListResponse> {
        return apiInterface.getResources()
    }

    override suspend fun getResourceDetail(resourceId: String): Response<ResourceDetailResponse> {
        return apiInterface.getResourceDetail(
            url = ApiInterface.getResourceDetailUrl(resourceId = resourceId)
        )
    }

    override suspend fun addResource(request: AddResourceRequest): Response<AddResourceResponse> {
        return apiInterface.addResource(request = request)
    }

    override suspend fun editResource(resourceId: String, request: AddResourceRequest): Response<AddResourceResponse> {
        return apiInterface.editResource(
            url = ApiInterface.getEditResourceUrl(resourceId), request)
    }

    override suspend fun deleteResource(resourceId: String): Response<Unit> {
        return apiInterface.deleteResource(
            url = ApiInterface.getDeleteResourceUrl(resourceId)
        )
    }
}