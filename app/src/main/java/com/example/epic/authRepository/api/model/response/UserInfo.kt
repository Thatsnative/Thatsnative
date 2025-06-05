package com.example.epic.authRepository.api.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: Int,
    @SerializedName("last_name")
    val lastName: Int,
    @SerializedName("email")
    val email: Int,
    @SerializedName("date_joined")
    val joinedDate: String,
)