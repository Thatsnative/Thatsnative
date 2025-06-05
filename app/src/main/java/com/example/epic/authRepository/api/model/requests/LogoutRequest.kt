package com.example.epic.authRepository.api.model.requests

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("refresh_token")
    val token: String
)