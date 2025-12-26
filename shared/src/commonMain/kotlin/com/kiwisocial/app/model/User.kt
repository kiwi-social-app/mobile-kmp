package com.kiwisocial.app.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val posts: List<Post>,
    val chats: List<Chat>,
    val favorites: List<String>,
    val likedPosts: List<String>,
    val dislikedPosts: List<String>
)