package com.example.epic.authRepository.api.model.response

data class SignInResponse(
    val success: Boolean,
    val error: String? = null,
)
