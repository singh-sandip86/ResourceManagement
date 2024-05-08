package com.rm.data.project

import com.rm.utils.empty

data class ProjectListItem(
    val id: String = String.empty(),
    val parentProjectId: String = String.empty(),
    val parentProjectName: String = String.empty(),
    val projectName: String = String.empty(),
    val projectType: List<String> = listOf(), // project_scope_id
    val projectStatus: String = String.empty(), // project_state
    val projectStatusColor: Int = 0,
    val projectStatusBackgroundColor: Int = 0,
    val projectStartDate: Long = 0L,
    val projectEndDate: Long = 0L,
    val client: String = String.empty(),
    val clientColor: Int = 0,
    val clientBackgroundColor: Int = 0,
    val resourceTypeList: List<ResourceTypeItem> = listOf(),
    val resourceList: List<ResourceItem> = listOf(),
    val isSubProject: Boolean = false,
)

data class ResourceTypeItem(
    val designationType: String,
    val progress: Float,
    val totalCount: Int,
    val allocationCount: Int,
    val indicatorColor: Int,
    val indicatorBackgroundColor: Int
)

data class ResourceItem(
    val id: String,
    val name: String,
    val email: String,
    val contactNumber: String,
    val designation: String,
    val allocationType: String, // Full/Half/Hourly
    val allocatedHours: Int,
    val allocatedFrom: Long,
    val allocatedTill: Long
)
