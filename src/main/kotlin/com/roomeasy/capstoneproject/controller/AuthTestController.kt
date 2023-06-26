package com.roomeasy.capstoneproject.controller

import com.roomeasy.capstoneproject.common.auth.JwtTokenProvider
import com.roomeasy.capstoneproject.controller.dto.AuthTestRequestDto
import com.roomeasy.capstoneproject.controller.dto.LoginResponse
import com.roomeasy.capstoneproject.controller.dto.RefreshTestRequestDto
import com.roomeasy.capstoneproject.repository.user.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test/auth")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class AuthTestController(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
) {
    @PostMapping("/login")
    fun test(@RequestBody requestBody: AuthTestRequestDto): ResponseEntity<LoginResponse> {
        val token = jwtTokenProvider.generateTokenForTest(requestBody.accessTokenExpiredTime, requestBody.refreshTokenExpiredTime)
        return ResponseEntity.ok().body(
            LoginResponse(
                HttpStatus.OK.value(),true,"로그인 성공", token.first, token.second
        ))
    }

    @PostMapping("/refresh")
    fun refreshTest(@RequestBody requestBody: RefreshTestRequestDto): ResponseEntity<LoginResponse> {
        if(!jwtTokenProvider.validateRefreshToken(requestBody.refreshToken)){
            throw JwtException("Invalid refresh token")
        }
        val claims: Claims = jwtTokenProvider.getUserIdFromRefreshToken(requestBody.refreshToken).getOrThrow()
        val userId: Long = claims["id"]?.let {
            userRepository.findById((it as Int).toLong()).get().id
        } ?: throw JwtException("Failed to retrieve user details from JWT token")

        val token = jwtTokenProvider.generateTokenForTest(requestBody.accessTokenExpiredTime, requestBody.refreshTokenExpiredTime)
        return ResponseEntity.ok().body(
            LoginResponse(
                HttpStatus.OK.value(),true,"토큰 재발급 성공", token.first, token.second
        ))
    }
}