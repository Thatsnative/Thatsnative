package com.example.epic.authRepository.api.model.response

import com.google.gson.annotations.SerializedName

data class UpdateUserResponse(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)
