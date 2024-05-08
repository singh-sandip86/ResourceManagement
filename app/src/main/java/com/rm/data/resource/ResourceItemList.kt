package com.rm.data.resource

import android.os.Parcelable
import com.rm.utils.empty
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResourceItemList(
    val id: String = String.empty(),
    val name: String = String.empty(),
    val email: String = String.empty(),
    val contactNumber: String = String.empty(),
    val resourceType: String = String.empty(),
    val profileImage: String = String.empty(),
    val nameProfileBackgroundColor: Int = 0,
    val designation: String = String.empty(),
    val designationBackgroundColor: Int = 0,
    val technology: List<String> = listOf(),
    val dateOfJoining: String = String.empty(),
    val linkedInProfile: String = "www.linkedin.com/in/sandip-singh-2b77b6101", //String.empty(),
    val compensation: String = String.empty(),
    val occupancy: Occupancy = Occupancy(),
    val projectList: List<Occupancy> = listOf()
): Parcelable

@Parcelize
data class Occupancy(
    val projectName: String = String.empty(),
    val occupancy: Float = 0.0f,
    val occupancyHours: String = "0hr",
    val occupancyColor: Int = 0,
    val occupancyBackgroundColor: Int = 0
): Parcelable