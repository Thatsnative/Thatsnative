package com.example.epic.common

object ValidatorUtils {
    private const val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$"
    private const val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"


    fun validatePassword(password: String) = PASSWORD_REGEX.toRegex().matches(password)
    fun validateEmail(email: String) = EMAIL_REGEX.toRegex().matches(email)
}