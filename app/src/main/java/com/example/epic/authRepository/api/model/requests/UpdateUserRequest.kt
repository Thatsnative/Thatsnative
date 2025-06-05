package com.example.epic.authRepository.api.model.requests

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
