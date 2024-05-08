package com.rm.api.request.project

data class SubProject(
    val project_name: String,
    val project_description: String,
    val project_state: Int,
    val project_scope_id: Int,
    val start_date: Long,
    val end_date: Long,
    val resource_type: List<ResourceType>,
    val sub_project_resources: List<SubProjectResource>
)