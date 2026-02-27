package com.kiwisocial.app.data

import com.kiwisocial.app.baseUrl
import com.kiwisocial.app.model.Comment
import com.kiwisocial.app.model.CreateComment
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CommentDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    private val commentsUrl = "$baseUrl/api/comments"

    private suspend fun getAuthToken(): String? {
        return Firebase.auth.currentUser?.getIdToken(false)
    }

    suspend fun getCommentsByPostId(postId: String): List<Comment>{
        return client.get("$commentsUrl/$postId"){
            getAuthToken()?.let { bearerAuth(it) }
        }.body()
    }

    suspend fun createComment(postId: String, createComment: CreateComment): Comment{
        return client.post(commentsUrl) {
            contentType(ContentType.Application.Json)
            getAuthToken()?.let { bearerAuth(it) }
            parameter("postId", postId)
            setBody(createComment)
        }.body()
    }
}