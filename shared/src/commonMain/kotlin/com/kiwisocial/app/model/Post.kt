@file:OptIn(ExperimentalTime::class)

package com.kiwisocial.app.model

import kotlin.time.ExperimentalTime
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String?,
    val published: Boolean,
    val author: UserBasic,
    val favoritedBy: List<String>,
    val likedByUsers: List<String>,
    val dislikedByUsers: List<String>,
)

@Serializable
data class CreatePost(val body: String)

@Serializable
data class UpdatePost(val body: String)
