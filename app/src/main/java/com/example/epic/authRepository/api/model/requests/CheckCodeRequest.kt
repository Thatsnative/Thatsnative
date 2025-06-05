package com.example.epic.authRepository.api.model.requests

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CheckCodeRequest (
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String
)