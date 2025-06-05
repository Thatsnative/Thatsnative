package com.example.epic.authRepository.api.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CheckCodeResponse(
    @SerializedName("access")
    val accessToken: String? = null,
    @SerializedName("refresh")
    val refreshToken: String? = null,
    @SerializedName("error")
    val error: String? = null,
)