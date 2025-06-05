package com.example.epic.authRepository.api.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerializedName("access")
    val accessToken: String,
    @SerializedName("refresh")
    val refreshToken: String?,
)
