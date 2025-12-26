@file:OptIn(ExperimentalTime::class)

package com.kiwisocial.app.model

import kotlin.time.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class Post(
    val id: String,
    val body: String,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant?,
    val published: Boolean,
    val author: UserBasic,
    val favoritedBy: List<String>,
    val likedByUsers: List<String>,
    val dislikedByUsers: List<String>,
    )

@Serializable
data class CreatePost(
    val body: String,
)