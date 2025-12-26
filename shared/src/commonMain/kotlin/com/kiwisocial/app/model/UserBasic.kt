package com.kiwisocial.app.model

import kotlinx.serialization.Serializable

@Serializable
data class UserBasic (
    val id: String,
    val username: String?,
    val email: String?
)