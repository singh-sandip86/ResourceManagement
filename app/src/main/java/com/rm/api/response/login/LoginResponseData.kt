package com.rm.api.response.login

data class LoginResponseData(
    val id: String,
    val name: String,
    val email: String,
    val contact_number: String,
    val designation: Int,
    val app_user_role_id: Int,
    val technologies: List<Int>,
    val token: String
)