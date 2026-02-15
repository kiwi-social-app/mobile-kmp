package com.kiwisocial.app.data

import com.kiwisocial.app.baseUrl
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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.kiwisocial.app.model.CreatePost
import com.kiwisocial.app.model.Post
import io.ktor.client.request.parameter
import io.ktor.http.ContentType

class PostDataSource {

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

    private val postsUrl = "$baseUrl/api/posts"

    private suspend fun getAuthToken(): String? {
        return Firebase.auth.currentUser?.getIdToken(false)
    }

    suspend fun getAllPosts(): List<Post> {
        return client.get(postsUrl) {
            getAuthToken()?.let { bearerAuth(it) }
        }.body()
    }


    suspend fun createPost(createPost: CreatePost): Post {
        return client.post(postsUrl) {
            contentType(ContentType.Application.Json)
            getAuthToken()?.let { bearerAuth(it) }
            setBody(createPost)
        }.body()
    }
}