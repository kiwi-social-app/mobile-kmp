package com.kiwisocial.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String?,
    val author: UserBasic,
)

@Serializable
data class CreateComment(val body: String)
