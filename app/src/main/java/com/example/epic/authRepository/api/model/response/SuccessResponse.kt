package com.example.epic.authRepository.api.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SuccessResponse(
    @SerializedName("success")
    val success: Boolean
)
