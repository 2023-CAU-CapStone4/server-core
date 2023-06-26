package com.roomeasy.capstoneproject.common.auth

import com.roomeasy.capstoneproject.domain.user.SecurityUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.sql.Timestamp


@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String,
    @Value("\${jwt.access-expires-in}")
    private val jwtExpirationInMs: Long,
    @Value("\${jwt.refresh-secret}")
    private val jwtRefreshSecret: String,
    @Value("\${jwt.refresh-expires-in}")
    private val jwtRefreshExpirationInMs: Long,
) {
    fun generateAccessTokenWithAuthentication(authentication: Authentication): String {
        val userPrincipal = authentication.details as SecurityUser
        val now = System.currentTimeMillis()
        val expiryDate = Timestamp(now + jwtExpirationInMs * 1000)

        val claims = Jwts.claims()
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray().copyOf(64))
        claims["id"] = userPrincipal.getUserId()

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Timestamp(now))
            .setIssuer("Morgan")
            .setExpiration(expiryDate)
            .setSubject("userInfo")
            .setHeaderParam("typ", "JWT")
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUserIdFromToken(token: String?): Result<Claims> {
        return kotlin.runCatching {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret.toByteArray().copyOf(64))
                .build()
                .parseClaimsJws(token)
                .body
        }.onFailure {
            return Result.failure(it)
        }

    }

    fun getUserIdFromRefreshToken(token: String?): Result<Claims> {
        return kotlin.runCatching {
            Jwts.parserBuilder()
                .setSigningKey(jwtRefreshSecret.toByteArray().copyOf(64))
                .build()
                .parseClaimsJws(token)
                .body
        }.onFailure {
            return Result.failure(it)
        }

    }

    fun validateToken(authToken: String?): Boolean {
        Jwts.parserBuilder()
            .setSigningKey(jwtSecret.toByteArray().copyOf(64))
            .build()
            .parseClaimsJws(authToken)
            .body
        return true
    }

    fun validateRefreshToken(refreshToken: String): Boolean {
        Jwts.parserBuilder()
            .setSigningKey(jwtRefreshSecret.toByteArray().copyOf(64))
            .build()
            .parseClaimsJws(refreshToken)
            .body
        return true
    }

    fun generateTokenByUserId(userId: Long): Pair<String, String> {
        val now = System.currentTimeMillis()
        val refreshToken = generateToken(now, userId, jwtRefreshExpirationInMs, jwtRefreshSecret)
        val accessToken = generateToken(now, userId, jwtExpirationInMs, jwtSecret)
        return Pair(accessToken, refreshToken)
    }

    fun generateTokenForTest(expiredTime: Long, refreshTokenExpiredTime: Long): Pair<String, String> {
        val now = System.currentTimeMillis()
        val refreshToken = generateToken(now, 1, refreshTokenExpiredTime, jwtRefreshSecret)
        val accessToken = generateToken(now, 1, expiredTime, jwtSecret)
        return Pair(accessToken, refreshToken)
    }

    private fun generateToken(now: Long, userId: Long, expireTime: Long, secretKey: String): String {
        val expiryDate = Timestamp(now + expireTime * 1000)

        val claims = Jwts.claims()
        val key = Keys.hmacShaKeyFor(secretKey.toByteArray().copyOf(64))
        claims["id"] = userId

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Timestamp(now))
            .setIssuer("Morgan")
            .setExpiration(expiryDate)
            .setSubject("userInfo")
            .setHeaderParam("typ", "JWT")
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}