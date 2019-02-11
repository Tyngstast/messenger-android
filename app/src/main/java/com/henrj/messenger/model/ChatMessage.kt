package com.henrj.messenger.model

import java.time.Instant

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val fromId: String = "" ,
    val toId: String = "",
    val timestamp: Long = System.currentTimeMillis() / 1000
)