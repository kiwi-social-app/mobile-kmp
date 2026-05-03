package com.kiwisocial.app.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(val content: String, val score: Double, val postId: String?)
