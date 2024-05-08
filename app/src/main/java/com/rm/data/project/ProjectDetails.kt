package com.rm.data.project

import android.os.Parcelable
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.project.ProjectResource
import com.rm.api.response.project.SubProjectResource
import com.rm.utils.empty
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectDetails (
    val projectId: String = String.empty(),
    val projectName: String = String.empty(),
    val projectImage: String = String.empty(),
    val projectDescription: String = String.empty(),
    val projectType: List<String> = listOf(), // project_scope_id
    val projectStatus: String = String.empty(), // project_state
    val projectStatusColor: Int = 0,
    val projectStatusBackgroundColor: Int = 0,
    val projectStartDate: Long = 0L,
    val projectEndDate: Long = 0L,
    val clientId: Int = 0,
    val clientName: String = String.empty(),
    val clientImage: String = String.empty(),
    val projectResourceList: List<ProjectResources> = listOf(),
    val subProjectList: List<SubProjects> = listOf()
): Parcelable

@Parcelize
data class ProjectResources(
    val resourceTypeId: Int,
    val resourceType: String = String.empty(),
    val totalCount: Int = 0,
    var allocationCount: Int = 0,
    val countColor: Int = 0,
    val countBackgroundColor: Int = 0,
    var resourceList: List<Resource> = listOf()
): Parcelable

@Parcelize
data class Resource(
    val id: String = String.empty(),
    val resourceId: String = String.empty(),
    val resourceName: String = String.empty(),
    val resourceImage: String = String.empty(),
    val designation: String = String.empty(),
    val allocatedDesignation: String = String.empty(),
    val allocatedHours: String = String.empty(),
    val hoursProgressIndicator: Float = 0.0f,
    val hoursProgressIndicatorColor: Int = 0,
    val hoursProgressIndicatorBackgroundColor: Int = 0,
    val allocatedFrom: Long = 0L,
    val allocatedTill: Long = 0L
): Parcelable

@Parcelize
data class SubProjects(
    val subProjectId: String = String.empty(),
    val subProjectName: String = String.empty(),
    val subProjectDescription: String = String.empty(),
    val subProjectType: String = String.empty(), // project_scope_id
    val subProjectStatus: String = String.empty(), // project_state
    val subProjectStatusColor: Int = 0,
    val subProjectStatusBackgroundColor: Int = 0,
    val subProjectStartDate: Long = 0L,
    val subProjectEndDate: Long = 0L,
    val subProjectResourceList: List<ProjectResources> = listOf(),
): Parcelable


fun List<Resource>.toProjectResource(masterData: MasterDataResponseData): List<ProjectResource> {
    val list = mutableListOf<ProjectResource>()

    this.forEach {
        var designationId =  0
        var designation =  0
        masterData.designation_types.forEach { type ->
            if (type.value.equals(it.allocatedDesignation, ignoreCase = true)) {
                designationId = type.id
            }
            if (type.value.equals(it.designation, ignoreCase = true)) {
                designation = type.id
            }
        }
        list.add(
            ProjectResource(
                allocated_from = it.allocatedFrom,
                allocated_to = it.allocatedTill,
                allocated_hours = it.allocatedHours.filter { it.isLetter().not() }.trim().toInt(),
                allocation_id = 2,
                contact_number = String.empty(),
                designation = designation,
                designation_id = designationId,
                email = String.empty(),
                id = String.empty(),
                name = it.resourceName,
                project_id = String.empty(),
                resource_id = it.resourceId
            )
        )
    }

    return list
}

fun List<Resource>.toSubProjectResource(masterData: MasterDataResponseData): List<SubProjectResource> {
    val list = mutableListOf<SubProjectResource>()

    this.forEach {
        var designationId =  0
        var designation =  0
        masterData.designation_types.forEach { type ->
            if (type.value.equals(it.allocatedDesignation, ignoreCase = true)) {
                designationId = type.id
            }
            if (type.value.equals(it.designation, ignoreCase = true)) {
                designation = type.id
            }
        }
        list.add(
            SubProjectResource(
                allocated_from = it.allocatedFrom,
                allocated_to = it.allocatedTill,
                allocated_hours = it.allocatedHours.filter { it.isLetter().not() }.trim().toInt(),
                allocation_id = 2,
                contact_number = String.empty(),
                designation = designation,
                designation_id = designationId,
                email = String.empty(),
                id = String.empty(),
                name = it.resourceName,
                project_id = String.empty(),
                resource_id = it.resourceId,
                sub_project_id = String.empty()
            )
        )
    }

    return list
}