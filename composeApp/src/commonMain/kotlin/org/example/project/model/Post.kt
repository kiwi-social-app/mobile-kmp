package org.example.project.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class Post(
    val id: String,
    val body: String,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant?,
    val published: Boolean,
    val authorId: String
    )