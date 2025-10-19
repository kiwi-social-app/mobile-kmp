@file:OptIn(kotlin.time.ExperimentalTime::class)

package org.example.project.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Message(
    val senderId: String,
    val content: String,
    @Contextual
    val timestamp: Instant
)