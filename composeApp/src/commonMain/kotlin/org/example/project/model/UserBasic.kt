package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class UserBasic (
    val id: String,
    val username: String?
)