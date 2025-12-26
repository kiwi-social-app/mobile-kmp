package com.kiwisocial.app.data

import com.kiwisocial.app.baseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.kiwisocial.app.model.CreatePost
import com.kiwisocial.app.model.Post
import io.ktor.http.ContentType

class PostDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val postsUrl = "$baseUrl/api/posts"

    suspend fun getAllPosts(): List<Post> {
        return client.get(postsUrl).body()
    }


    suspend fun createPost(userId: String, createPost: CreatePost): Post {
        return client.post(postsUrl) {
            contentType(ContentType.Application.Json)
            parameter("userId", userId)
            setBody(createPost)
        }.body()
    }
}