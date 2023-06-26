package com.roomeasy.capstoneproject.controller.dto

data class AuthTestRequestDto(
    val id: String,
    val password: String,
    val accessTokenExpiredTime: Long,
    val refreshTokenExpiredTime: Long,
)
