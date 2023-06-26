package com.roomeasy.capstoneproject.controller.dto

data class RefreshTestRequestDto(
    val refreshToken: String,
    val accessTokenExpiredTime: Long,
    val refreshTokenExpiredTime: Long,
)
