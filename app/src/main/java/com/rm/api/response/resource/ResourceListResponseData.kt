package com.rm.api.response.resource

import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.data.resource.Occupancy
import com.rm.data.resource.ResourceItemList
import com.rm.utils.empty

data class ResourceListResponseData(
    val resources: List<Resource>
)

data class Resource(
    val app_user_role_id: Int,
    val contact_number: String? = null,
    val designation: Int,
    val email: String,
    val id: String,
    val name: String,
    val password: String,
    val technologies: List<Int>? = null,
    val projects: List<Projects>,
    val subproject: List<SubProjects>
)

data class Projects(
    val project_name: String,
    val allocated_hours: Int
)

data class SubProjects(
    val sub_project_name: String,
    val allocated_hours: Int
)

fun Resource.toResourceItemList(masterData: MasterDataResponseData): ResourceItemList {

    var resourceDesignation = String.empty()
    masterData.designation_types.forEach { designationType ->
        if (designationType.id == designation) {
            resourceDesignation = designationType.value
        }
    }
    val resourceTechnology = mutableListOf<String>()
    masterData.technologies.forEach { technology ->
        technologies?.let {
            if (it.contains(technology.id)) {
                resourceTechnology.add(technology.value)
            }
        }
    }

    var resourceOccupancy = 0.0f
    var resourceOccupancyPercentage = 0
    var totalAllocatedTime = 0
    projects.forEach { project ->
        totalAllocatedTime += project.allocated_hours
    }
    subproject.forEach { subProjects ->
        totalAllocatedTime += subProjects.allocated_hours
    }
    resourceOccupancyPercentage = (totalAllocatedTime * 100) / 8
    resourceOccupancy = resourceOccupancyPercentage.toFloat() / 100

    val occupancy = Occupancy(
        occupancy = if (totalAllocatedTime >= 8) 1.0f else resourceOccupancy,
        occupancyHours = totalAllocatedTime.toString()
            .plus(if (totalAllocatedTime > 1) "hrs" else "hr"),
        occupancyColor = if (totalAllocatedTime >= 8) R.color.red_6 else if (totalAllocatedTime <= 4) R.color.green_6 else R.color.orange_6,
        occupancyBackgroundColor = if (totalAllocatedTime >= 8) R.color.red_1 else if (totalAllocatedTime <= 4) R.color.green_1 else R.color.orange_1
    )

    val projectList = mutableListOf<Occupancy>()

    projects.forEach { project ->
        projectList.add(
            Occupancy(
                projectName = project.project_name,
                occupancy = if (project.allocated_hours >= 8) 1.0f else (project.allocated_hours.toFloat()) / 8,
                occupancyHours = project.allocated_hours.toString()
                    .plus(if (project.allocated_hours > 1) "hrs" else "hr"),
                occupancyColor = if (project.allocated_hours >= 8) R.color.red_6 else if (project.allocated_hours <= 4) R.color.green_6 else R.color.orange_6,
                occupancyBackgroundColor = if (project.allocated_hours >= 8) R.color.red_1 else if (project.allocated_hours <= 4) R.color.green_1 else R.color.orange_1
            )
        )
    }

    subproject.forEach { project ->
        projectList.add(
            Occupancy(
                projectName = project.sub_project_name,
                occupancy = if (project.allocated_hours >= 8) 1.0f else (project.allocated_hours.toFloat()) / 8,
                occupancyHours = project.allocated_hours.toString()
                    .plus(if (project.allocated_hours > 1) "hrs" else "hr"),
                occupancyColor = if (project.allocated_hours >= 8) R.color.red_6 else if (project.allocated_hours <= 4) R.color.green_6 else R.color.orange_6,
                occupancyBackgroundColor = if (project.allocated_hours >= 8) R.color.red_1 else if (project.allocated_hours <= 4) R.color.green_1 else R.color.orange_1
            )
        )
    }

    var resourceType = String.empty()
    masterData.user_role_types.forEach { userRole ->
        if (userRole.id == app_user_role_id) {
            resourceType = userRole.value
        }
    }

    return ResourceItemList(
        id = id,
        name = name,
        email = email,
        contactNumber = contact_number ?: String.empty(),
        resourceType = resourceType,
        profileImage = String.empty(),
        nameProfileBackgroundColor = R.color.orange_1,
        designation = resourceDesignation,
        designationBackgroundColor = R.color.cyan_1,
        technology = resourceTechnology,
        occupancy = occupancy,
        projectList = projectList
    )
}


fun List<Resource>.toResourceItemList(masterData: MasterDataResponseData): List<ResourceItemList> {

    val resourceItemList = mutableListOf<ResourceItemList>()

    this.forEach { resourceItem ->
        var resourceDesignation = String.empty()
        masterData.designation_types.forEach { designationType ->
            if (designationType.id == resourceItem.designation) {
                resourceDesignation = designationType.value
            }
        }
        val resourceTechnology = mutableListOf<String>()
        masterData.technologies.forEach { technology ->
            resourceItem.technologies?.let {
                if (it.contains(technology.id)) {
                    resourceTechnology.add(technology.value)
                }
            }
        }

        var resourceOccupancy = 0.0f
        var resourceOccupancyPercentage = 0
        var totalAllocatedTime = 0
        resourceItem.projects.forEach { project ->
            totalAllocatedTime += project.allocated_hours
        }
        resourceItem.subproject.forEach { subProjects ->
            totalAllocatedTime += subProjects.allocated_hours
        }
        resourceOccupancyPercentage = (totalAllocatedTime * 100) / 8
        resourceOccupancy = resourceOccupancyPercentage.toFloat() / 100

        val occupancy = Occupancy(
            occupancy = if (totalAllocatedTime >= 8) 1.0f else resourceOccupancy,
            occupancyHours = totalAllocatedTime.toString()
                .plus(if (totalAllocatedTime > 1) "hrs" else "hr"),
            occupancyColor = if (totalAllocatedTime >= 8) R.color.red_6 else if (totalAllocatedTime <= 4) R.color.green_6 else R.color.orange_6,
            occupancyBackgroundColor = if (totalAllocatedTime >= 8) R.color.red_1 else if (totalAllocatedTime <= 4) R.color.green_1 else R.color.orange_1
        )

        val projectList = mutableListOf<Occupancy>()

        resourceItem.projects.forEach { project ->
            projectList.add(
                Occupancy(
                    projectName = project.project_name,
                    occupancy = if (project.allocated_hours >= 8) 1.0f else (project.allocated_hours.toFloat()) / 8,
                    occupancyHours = project.allocated_hours.toString()
                        .plus(if (project.allocated_hours > 1) "hrs" else "hr"),
                    occupancyColor = if (project.allocated_hours >= 8) R.color.red_6 else if (project.allocated_hours <= 4) R.color.green_6 else R.color.orange_6,
                    occupancyBackgroundColor = if (project.allocated_hours >= 8) R.color.red_1 else if (project.allocated_hours <= 4) R.color.green_1 else R.color.orange_1
                )
            )
        }

        resourceItem.subproject.forEach { project ->
            projectList.add(
                Occupancy(
                    projectName = project.sub_project_name,
                    occupancy = if (project.allocated_hours >= 8) 1.0f else (project.allocated_hours.toFloat()) / 8,
                    occupancyHours = project.allocated_hours.toString()
                        .plus(if (project.allocated_hours > 1) "hrs" else "hr"),
                    occupancyColor = if (project.allocated_hours >= 8) R.color.red_6 else if (project.allocated_hours <= 4) R.color.green_6 else R.color.orange_6,
                    occupancyBackgroundColor = if (project.allocated_hours >= 8) R.color.red_1 else if (project.allocated_hours <= 4) R.color.green_1 else R.color.orange_1
                )
            )
        }

        var resourceType = String.empty()
        masterData.user_role_types.forEach { userRole ->
            if (userRole.id == resourceItem.app_user_role_id) {
                resourceType = userRole.value
            }
        }

        resourceItemList.add(
            ResourceItemList(
                id = resourceItem.id,
                name = resourceItem.name,
                email = resourceItem.email,
                contactNumber = resourceItem.contact_number ?: String.empty(),
                resourceType = resourceType,
                profileImage = String.empty(),
                nameProfileBackgroundColor = R.color.orange_1,
                designation = resourceDesignation,
                designationBackgroundColor = R.color.cyan_1,
                technology = resourceTechnology,
                occupancy = occupancy,
                projectList = projectList
            )
        )
    }

    return resourceItemList
}