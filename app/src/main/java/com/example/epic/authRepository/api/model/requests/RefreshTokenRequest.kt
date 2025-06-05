package com.example.epic.authRepository.api.model.requests

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    @SerializedName("refresh")
    val refreshToken: String
)
