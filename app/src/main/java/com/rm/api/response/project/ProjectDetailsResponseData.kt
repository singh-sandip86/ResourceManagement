package com.rm.api.response.project

import android.os.Parcelable
import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.data.project.ProjectDetails
import com.rm.data.project.ProjectResources
import com.rm.data.project.Resource
import com.rm.data.project.SubProjects
import com.rm.utils.empty
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectDetailsResponseData(
    val client: Int = 0,
    val end_date: Long = 0L,
    val id: String = String.empty(),
    val project_name: String = String.empty(),
    var project_resources: MutableList<ProjectResource> = mutableListOf(),
    val project_scope: List<Int> = listOf(),
    val project_state: Int = 0,
    val resource_type: MutableList<ResourceType> = mutableListOf(),
    val start_date: Long = 0L,
    val sub_projects: List<SubProject> = listOf()
): Parcelable

fun ProjectDetailsResponseData.toProjectDetails(
    masterData: MasterDataResponseData
): ProjectDetails {
    var clientName = String.empty()
    masterData.clients.forEach {
        if (it.id == client) {
            clientName = it.value
        }
    }

    val projectScope = mutableListOf<String>()
    masterData.project_scope_types.forEach { projectScopeType ->
        this.project_scope.forEach {
            if (projectScopeType.id == it) {
                projectScope.add(projectScopeType.value)
            }
        }
    }

    var projectState = String.empty()
    masterData.project_states.forEach { state ->
        if (state.id == this.project_state) {
            projectState = state.value
        }
    }

    val projectResourceList = mutableListOf<ProjectResources>()
    this.resource_type.forEach { resourceType ->
        var resourceDesignation = String.empty()
        masterData.designation_types.forEach { designationType ->
            if (designationType.id == resourceType.designation_id) {
                resourceDesignation = designationType.value
            }
        }
        var allocationCount = 0
        val resourceItemList = mutableListOf<Resource>()
        this.project_resources.forEach { resource ->
            if (resource.designation_id == resourceType.designation_id) {
                allocationCount++

                var rDesignation = String.empty()
                masterData.designation_types.forEach { designationType ->
                    if (designationType.id == resource.designation) {
                        rDesignation = designationType.value
                    }
                }
                var allocatedDesignation = String.empty()
                masterData.designation_types.forEach { designationType ->
                    if (designationType.id == resource.designation_id) {
                        allocatedDesignation = designationType.value
                    }
                }

                val rProgress = resource.allocated_hours.toFloat() / 8.0f

//            var resourceAllocation = String.empty()
//            masterData.allocation_types.forEach { allocationType ->
//                if (allocationType.id == projectResource.allocation_id) {
//                    resourceAllocation = allocationType.value
//                }
//            }

                val resourceItem = Resource(
                    id = resource.id,
                    resourceId = resource.resource_id,
                    resourceName = resource.name,
                    resourceImage = String.empty(),
                    designation = rDesignation,
                    allocatedDesignation = allocatedDesignation,
                    allocatedHours = resource.allocated_hours.toString()
                        .plus(if (resource.allocated_hours > 1) " hrs" else " hr"),
                    hoursProgressIndicator = rProgress,
                    hoursProgressIndicatorColor = if (rProgress >= 1.0f) R.color.red_6 else if (rProgress <= 0.5f) R.color.green_6 else R.color.orange_6,
                    hoursProgressIndicatorBackgroundColor = if (rProgress >= 1.0f) R.color.red_1 else if (rProgress <= 0.5f) R.color.green_1 else R.color.orange_1,
                    allocatedFrom = resource.allocated_from,
                    allocatedTill = resource.allocated_to
                )
                resourceItemList.add(resourceItem)
            }
        }
        val progress = allocationCount.toFloat() / resourceType.count.toFloat()

        val projectResource = ProjectResources(
            resourceTypeId = resourceType.designation_id,
            resourceType = resourceDesignation,
            totalCount = resourceType.count,
            allocationCount = allocationCount,
            countColor = if (progress >= 1.0f) R.color.green_6 else if (progress <= 0.5f) R.color.red_6 else R.color.orange_6,
            countBackgroundColor = if (progress >= 1.0f) R.color.green_1 else if (progress <= 0.5f) R.color.red_1 else R.color.orange_1,
            resourceList = resourceItemList
        )
        projectResourceList.add(projectResource)
    }

    val subProjectList = mutableListOf<SubProjects>()
    this.sub_projects.forEach { subProject ->

        var subProjectScope = String.empty()
        masterData.project_scope_types.forEach { projectScopeType ->
            if (projectScopeType.id == subProject.project_scope_id) {
                subProjectScope = projectScopeType.value
            }
        }

        var subProjectState = String.empty()
        masterData.project_states.forEach { state ->
            if (state.id == subProject.project_state) {
                subProjectState = state.value
            }
        }

        val subProjectResourceList = mutableListOf<ProjectResources>()
        subProject.resource_type.forEach { resourceType ->
            var resourceDesignation = String.empty()
            masterData.designation_types.forEach { designationType ->
                if (designationType.id == resourceType.designation_id) {
                    resourceDesignation = designationType.value
                }
            }
            var allocationCount = 0
            val subProjectResourceItemList = mutableListOf<Resource>()
            subProject.sub_project_resources.forEach { resource ->
                if (resource.designation_id == resourceType.designation_id) {
                    allocationCount++

                    var spResourceDesignation = String.empty()
                    masterData.designation_types.forEach { designationType ->
                        if (designationType.id == resource.designation) {
                            spResourceDesignation = designationType.value
                        }
                    }
                    var allocatedDesignation = String.empty()
                    masterData.designation_types.forEach { designationType ->
                        if (designationType.id == resource.designation_id) {
                            allocatedDesignation = designationType.value
                        }
                    }

                    val spProgress = resource.allocated_hours.toFloat() / 8.0f

                    val subProjectResourceItem = Resource(
                        id = resource.id,
                        resourceId = resource.resource_id,
                        resourceName = resource.name,
                        resourceImage = String.empty(),
                        designation = spResourceDesignation,
                        allocatedDesignation = allocatedDesignation,
                        allocatedHours = resource.allocated_hours.toString()
                            .plus(if (resource.allocated_hours > 1) " hrs" else " hr"),
                        hoursProgressIndicator = spProgress,
                        hoursProgressIndicatorColor = if (spProgress >= 1.0f) R.color.red_6 else if (spProgress <= 0.5f) R.color.green_6 else R.color.orange_6,
                        hoursProgressIndicatorBackgroundColor = if (spProgress >= 1.0f) R.color.red_1 else if (spProgress <= 0.5f) R.color.green_1 else R.color.orange_1,
                        allocatedFrom = resource.allocated_from,
                        allocatedTill = resource.allocated_to
                    )
                    subProjectResourceItemList.add(subProjectResourceItem)
                }
            }
            val progress = allocationCount.toFloat() / resourceType.count.toFloat()

            val subProjectResource = ProjectResources(
                resourceTypeId = resourceType.designation_id,
                resourceType = resourceDesignation,
                totalCount = resourceType.count,
                allocationCount = allocationCount,
                countColor = if (progress >= 1.0f) R.color.green_6 else if (progress <= 0.5f) R.color.red_6 else R.color.orange_6,
                countBackgroundColor = if (progress >= 1.0f) R.color.green_1 else if (progress <= 0.5f) R.color.red_1 else R.color.orange_1,
                resourceList = subProjectResourceItemList
            )
            subProjectResourceList.add(subProjectResource)
        }

        val subProjectItem = SubProjects(
            subProjectId = subProject.id,
            subProjectName = subProject.project_name,
            subProjectDescription = String.empty(),
            subProjectType = subProjectScope,
            subProjectStatus = subProjectState,
            subProjectStatusColor = R.color.green_6,
            subProjectStatusBackgroundColor = R.color.green_1,
            subProjectStartDate = subProject.start_date,
            subProjectEndDate = subProject.end_date,
            subProjectResourceList = subProjectResourceList
        )
        subProjectList.add(subProjectItem)
    }

    return ProjectDetails(
        projectId = this.id,
        projectName = this.project_name,
        projectImage = String.empty(),
        projectDescription = String.empty(),
        projectType = projectScope,
        projectStatus = projectState,
        projectStatusColor = R.color.green_6,
        projectStatusBackgroundColor = R.color.green_1,
        projectStartDate = this.start_date,
        projectEndDate = this.end_date,
        clientId = client,
        clientName = clientName,
        clientImage = String.empty(),
        projectResourceList = projectResourceList,
        subProjectList = subProjectList
    )
}