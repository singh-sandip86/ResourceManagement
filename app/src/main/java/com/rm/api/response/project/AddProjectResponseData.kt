package com.rm.api.response.project

data class AddProjectResponseData(
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