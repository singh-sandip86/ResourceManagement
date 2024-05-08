package com.rm.api.request.resource

import com.google.gson.annotations.SerializedName

data class AddResourceRequest(
    @SerializedName("app_user_role_id")
    val appUserRoleId: Int,
    @SerializedName("contact_number")
    val contactNumber: String? = null,
    @SerializedName("designation")
    val designation: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("technologies")
    val technologies: List<Int>,
    @SerializedName("date_of_joining")
    val dateOfJoining: String? = null,
    @SerializedName("linked_in_profile")
    val linkedInProfile: String? = null,
    @SerializedName("compensation")
    val compensation: String? = null,
)