package com.roomeasy.capstoneproject.service.dto

import java.time.LocalDateTime

data class ChatMessageDto(
    val id: Long,
    val sender: String,
    val userId: Long,
    val myMessage: Boolean,
    val content: String,
    val timestamp: LocalDateTime,
    val chatRoomId: Long
)
