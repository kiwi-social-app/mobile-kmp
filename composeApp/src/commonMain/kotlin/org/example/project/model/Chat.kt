package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: String,
    val messages: List<Message>,
    val participants: List<User>
)