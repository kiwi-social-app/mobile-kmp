package org.example.project.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val senderId: String,
    val content: String,
    val timestamp: Instant
)