package com.rm.api.response.resource

import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.data.resource.ResourceItemList
import com.rm.utils.empty

data class AddResourceResponseData(
    val app_user_role_id: Int,
    val contact_number: String,
    val designation: Int,
    val email: String,
    val id: String,
    val name: String,
    val password: String,
    val technologies: List<Int>
)

fun AddResourceResponseData.toResourceItemList(
    resource: ResourceItemList,
    masterData: MasterDataResponseData?
): ResourceItemList {

    var resourceDesignation = String.empty()
    masterData?.designation_types?.forEach { designationType ->
        if (designationType.id == designation) {
            resourceDesignation = designationType.value
        }
    }

    val resourceTechnology = mutableListOf<String>()
    masterData?.technologies?.forEach { technology ->
        if (technologies.contains(technology.id)) {
            resourceTechnology.add(technology.value)
        }
    }

    var resourceType = String.empty()
    masterData?.user_role_types?.forEach { userRole ->
        if (userRole.id == app_user_role_id) {
            resourceType = userRole.value
        }
    }

    return ResourceItemList(
        id = id,
        name = name,
        email = email,
        contactNumber = contact_number,
        resourceType = resourceType,
        profileImage = String.empty(),
        nameProfileBackgroundColor = R.color.orange_1,
        designation = resourceDesignation,
        designationBackgroundColor = R.color.cyan_1,
        technology = resourceTechnology,
        occupancy = resource.occupancy,
        projectList = resource.projectList
    )
}