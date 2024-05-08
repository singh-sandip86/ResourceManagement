package com.rm.api.response.project

import android.os.Parcelable
import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.data.project.ProjectListItem
import com.rm.data.project.ResourceItem
import com.rm.data.project.ResourceTypeItem
import com.rm.utils.empty
import kotlinx.parcelize.Parcelize

data class ProjectListResponseData(
    val projects: List<Project>
)

data class Project(
    val client: Int,
    val end_date: Long,
    val id: String,
    val project_name: String,
    val project_resources: List<ProjectResource>,
    val project_scope: List<Int>,
    val project_state: Int,
    val resource_type: List<ResourceType>,
    val start_date: Long,
    val sub_projects: List<SubProject>
)

@Parcelize
data class ProjectResource(
    val allocated_from: Long,
    val allocated_hours: Int,
    val allocated_to: Long,
    val allocation_id: Int? = null,
    val contact_number: String? = null,
    val designation: Int? = 0, // Actual Designation
    val designation_id: Int? = 0, // Allocated Designation
    val email: String? = null,
    val id: String,
    val name: String,
    val project_id: String? = null,
    val resource_id: String
): Parcelable

@Parcelize
data class ResourceType(
    val count: Int,
    val designation_id: Int
): Parcelable

@Parcelize
data class SubProject(
    val end_date: Long,
    val id: String,
    val project_id: String,
    val project_name: String,
    val project_scope_id: Int,
    val project_state: Int,
    val client: Int,
    val resource_type: MutableList<ResourceType> = mutableListOf(),
    val start_date: Long,
    var sub_project_resources: MutableList<SubProjectResource> = mutableListOf()
): Parcelable

@Parcelize
data class SubProjectResource(
    val allocated_from: Long,
    val allocated_hours: Int,
    val allocated_to: Long,
    val allocation_id: Int? = null,
    val contact_number: String? = null,
    val designation: Int? = 0, // Actual Designation
    val designation_id: Int? = 0, // Allocated Designation
    val email: String? = null,
    val id: String,
    val name: String,
    val project_id: String? = null,
    val resource_id: String,
    val sub_project_id: String? = null
): Parcelable

fun ProjectListResponseData.toProjectListItem(
    masterData: MasterDataResponseData
): List<ProjectListItem> {
    val projectItemList = mutableListOf<ProjectListItem>()

    projects.forEach { project ->
        val resourceTypeItemList = mutableListOf<ResourceTypeItem>()
        project.resource_type.forEach { resourceType ->
            var resourceDesignation = String.empty()
            masterData.designation_types.forEach { designationType ->
                if (designationType.id == resourceType.designation_id) {
                    resourceDesignation = designationType.value
                }
            }
            var allocationCount = 0
            project.project_resources.forEach { resource ->
                if (resource.designation_id == resourceType.designation_id) {
                    allocationCount++
                }
            }
            val progress = allocationCount.toFloat()/resourceType.count.toFloat()
            val resourceTypeItem = ResourceTypeItem(
                designationType = resourceDesignation,
                progress = progress,
                totalCount = resourceType.count,
                allocationCount = allocationCount,
                indicatorColor = if (progress>=1.0f) R.color.green_6 else if (progress<=0.5f) R.color.red_6 else R.color.orange_6,
                indicatorBackgroundColor = if (progress>=1.0f) R.color.green_1 else if (progress<=0.5f) R.color.red_1 else R.color.orange_1
            )
            resourceTypeItemList.add(resourceTypeItem)
        }

        val resourceItemList = mutableListOf<ResourceItem>()
        project.project_resources.forEach { projectResource ->
            var resourceDesignation = String.empty()
            masterData.designation_types.forEach { designationType ->
                if (designationType.id == projectResource.designation_id) {
                    resourceDesignation = designationType.value
                }
            }
            var resourceAllocation = String.empty()
            masterData.allocation_types.forEach { allocationType ->
                if (allocationType.id == projectResource.allocation_id) {
                    resourceAllocation = allocationType.value
                }
            }
            val resourceItem = ResourceItem(
                id = projectResource.id,
                name = projectResource.name,
                email = projectResource.email ?: String.empty(),
                contactNumber = projectResource.contact_number ?: String.empty(),
                designation = resourceDesignation,
                allocationType = resourceAllocation,
                allocatedHours = projectResource.allocated_hours,
                allocatedFrom = projectResource.allocated_from,
                allocatedTill = projectResource.allocated_to
            )
            resourceItemList.add(resourceItem)
        }

        val projectScope = mutableListOf<String>()
        masterData.project_scope_types.forEach { projectScopeType ->
            project.project_scope.forEach {
                if (projectScopeType.id == it) {
                    projectScope.add(projectScopeType.value)
                }
            }
        }
        var projectState = String.empty()
        masterData.project_states.forEach { state ->
            if (state.id == project.project_state) {
                projectState = state.value
            }
        }
        var clientName = String.empty()
        masterData.clients.forEach { client ->
            if (client.id == project.client) {
                clientName = client.value
            }
        }
        val projectItem = ProjectListItem(
            id = project.id,
            parentProjectId = project.id,
            parentProjectName = project.project_name,
            projectName = project.project_name,
            projectType = projectScope,
            projectStatus = projectState,
            projectStatusColor = R.color.green_6,
            projectStatusBackgroundColor = R.color.green_1,
            projectStartDate = project.start_date,
            projectEndDate = project.end_date,
            client = clientName,
            clientColor = R.color.gold_6,
            clientBackgroundColor = R.color.gold_1,
            resourceTypeList = resourceTypeItemList,
            resourceList = resourceItemList,
            isSubProject = false
        )
        projectItemList.add(projectItem)


        project.sub_projects.forEach { subProject ->
            val resourceTypeItemList = mutableListOf<ResourceTypeItem>()
            subProject.resource_type.forEach { resourceType ->
                var resourceDesignation = String.empty()
                masterData.designation_types.forEach { designationType ->
                    if (designationType.id == resourceType.designation_id) {
                        resourceDesignation = designationType.value
                    }
                }
                var allocationCount = 0
                subProject.sub_project_resources.forEach { resource ->
                    if (resource.designation_id == resourceType.designation_id) {
                        allocationCount++
                    }
                }
                val progress = allocationCount.toFloat()/resourceType.count.toFloat()
                val resourceTypeItem = ResourceTypeItem(
                    designationType = resourceDesignation,
                    progress = progress,
                    totalCount = resourceType.count,
                    allocationCount = allocationCount,
                    indicatorColor = if (progress>=1.0f) R.color.green_6 else if (progress<=0.5f) R.color.red_6 else R.color.orange_6,
                    indicatorBackgroundColor = if (progress>=1.0f) R.color.green_1 else if (progress<=0.5f) R.color.red_1 else R.color.orange_1
                )
                resourceTypeItemList.add(resourceTypeItem)
            }

            val resourceItemList = mutableListOf<ResourceItem>()
            subProject.sub_project_resources.forEach { subProjectResource ->
                var resourceDesignation = String.empty()
                masterData.designation_types.forEach { designationType ->
                    if (designationType.id == subProjectResource.designation_id) {
                        resourceDesignation = designationType.value
                    }
                }
                var resourceAllocation = String.empty()
                masterData.allocation_types.forEach { allocationType ->
                    if (allocationType.id == subProjectResource.allocation_id) {
                        resourceAllocation = allocationType.value
                    }
                }
                val resourceItem = ResourceItem(
                    id = subProjectResource.id,
                    name = subProjectResource.name,
                    email = subProjectResource.email ?: String.empty(),
                    contactNumber = subProjectResource.contact_number ?: String.empty(),
                    designation = resourceDesignation,
                    allocationType = resourceAllocation,
                    allocatedHours = subProjectResource.allocated_hours,
                    allocatedFrom = subProjectResource.allocated_from,
                    allocatedTill = subProjectResource.allocated_to
                )
                resourceItemList.add(resourceItem)
            }

            val projectScope = mutableListOf<String>()
            masterData.project_scope_types.forEach { projectScopeType ->
                if (projectScopeType.id == subProject.project_scope_id) {
                    projectScope.add(projectScopeType.value)
                }
            }
            var projectState = String.empty()
            masterData.project_states.forEach { state ->
                if (state.id == subProject.project_state) {
                    projectState = state.value
                }
            }
            var clientName = String.empty()
            masterData.clients.forEach { client ->
                if (client.id == subProject.client) {
                    clientName = client.value
                }
            }
            val projectItem = ProjectListItem(
                id = subProject.id,
                parentProjectId = project.id,
                parentProjectName = project.project_name,
                projectName = subProject.project_name,
                projectType = projectScope,
                projectStatus = projectState,
                projectStatusColor = R.color.green_6,
                projectStatusBackgroundColor = R.color.green_1,
                projectStartDate = subProject.start_date,
                projectEndDate = subProject.end_date,
                client = clientName,
                clientColor = R.color.gold_6,
                clientBackgroundColor = R.color.gold_1,
                resourceTypeList = resourceTypeItemList,
                resourceList = resourceItemList,
                isSubProject = true
            )
            projectItemList.add(projectItem)
        }
    }

    return projectItemList
}