package com.rm.api.response.masterdata

import com.rm.data.resource.ResourceProjectType
import com.rm.utils.empty

data class MasterDataResponseData(
    val allocation_types: List<Type>,
    val designation_types: List<Type>,
    val project_scope_types: List<Type>,
    val project_states: List<Type>,
    val technologies: List<Type>,
    val url_types: List<Type>,
    val user_role_types: List<Type>,
    val clients: List<Type>
)

data class Type(
    val id: Int = 0,
    val value: String = String.empty()
)

fun List<Type>.toResourceProjectType(): List<ResourceProjectType> {
    val resourceProjectType = mutableListOf<ResourceProjectType>()

    this.forEach { type ->
        resourceProjectType.add(
            ResourceProjectType(
                id = type.id.toString(),
                value = type.value
            )
        )
    }

    return resourceProjectType
}