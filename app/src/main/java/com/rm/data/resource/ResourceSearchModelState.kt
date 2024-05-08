package com.rm.data.resource

data class ResourceSearchModelState(
    val searchText: String = "",
    val resources: List<ResourceItemList> = arrayListOf(),
    val showProgressBar: Boolean = false
) {
    companion object {
        val Empty = ResourceSearchModelState()
    }
}