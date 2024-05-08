package com.rm.api.response.project

import com.rm.data.project.ProjectNameList
import com.rm.data.resource.ResourceProjectType

data class ProjectNameResponseData(
    val id: String,
    val project_name: String,
    val sub_projects_name: List<SubProjectsName>
)

data class SubProjectsName(
    val id: String,
    val project_name: String
)

fun List<ProjectNameResponseData>.toProjectList(): ProjectNameList {
    val projectNameList = mutableListOf<ResourceProjectType>()

    this.forEach {
        projectNameList.add(ResourceProjectType(id = it.id, value = it.project_name))
        it.sub_projects_name.forEach {
            projectNameList.add(ResourceProjectType(id = it.id, value = it.project_name))
        }
    }

    return ProjectNameList(projectNameList)
}