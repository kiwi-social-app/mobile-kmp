@file:OptIn(ExperimentalTime::class)

package com.kiwisocial.app.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Message(
    val senderId: String,
    val content: String,
    @Contextual
    val timestamp: Instant
)