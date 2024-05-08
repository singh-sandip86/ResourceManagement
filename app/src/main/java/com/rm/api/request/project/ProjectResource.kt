package com.rm.api.request.project

data class ProjectResource(
    val allocated_from: Long,
    val allocated_hours: Int,
    val allocated_to: Long,
    val allocation_id: Int,
    val designation_id: Int,
    val resource_id: String
)