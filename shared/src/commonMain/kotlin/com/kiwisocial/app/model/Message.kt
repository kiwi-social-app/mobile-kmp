package com.kiwisocial.app.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val chatId: String,
    val sender: UserBasic,
    val content: String,
    val timestamp: String
)

@Serializable
data class OutgoingMessage(
    val chatId: String,
    val sender: SenderRef,
    val content: String
)

@Serializable
data class SenderRef(val id: String)