package com.kiwisocial.app.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String?,
    val email: String,
    val firstname: String?,
    val lastname: String?,
    val chats: List<Chat>? = null,
    val favorites: List<String> = emptyList(),
    val likedPosts: List<String> = emptyList(),
    val dislikedPosts: List<String> = emptyList()
)

@Serializable
data class UserUpdate(
    val username: String?,
    val email: String,
    val firstname: String?,
    val lastname: String?
)