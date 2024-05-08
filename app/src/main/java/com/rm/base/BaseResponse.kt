package com.rm.base

import com.google.gson.annotations.SerializedName

abstract class BaseResponse<T> {
    @field:SerializedName("status_code")
    val statusCode: Int? = null

    @field:SerializedName("data")
    val data: T? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("success")
    val success: Boolean? = null
}