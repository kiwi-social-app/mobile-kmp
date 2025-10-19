package org.example.project.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.model.CreatePost
import org.example.project.model.Post

class PostDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val postsUrl = "http://10.0.2.2:8080/api/posts"

    suspend fun getAllPosts(): List<Post> {
        return client.get(postsUrl).body()
    }


    suspend fun createPost(userId: String, createPost: CreatePost): Post {
        return client.post(postsUrl) {
            contentType(io.ktor.http.ContentType.Application.Json)
            parameter("userId", userId)
            setBody(createPost)
        }.body()
    }
}