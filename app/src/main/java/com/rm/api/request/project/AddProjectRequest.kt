package com.rm.api.request.project

data class AddProjectRequest(
    val client: Int,
    val project_name: String,
    val project_description: String,
    val project_resources: List<ProjectResource>,
    val project_scope: List<Int>,
    val start_date: Long,
    val end_date: Long,
    val project_state: Int,
    val resource_type: List<ResourceType>,
    val sub_projects: List<SubProject>
)