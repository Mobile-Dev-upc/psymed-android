package com.example.psymed.data.model

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class SignUpRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String
)

data class AuthResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("role") val role: String,
    @SerializedName("token") val token: String
)

data class UserResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String
)

