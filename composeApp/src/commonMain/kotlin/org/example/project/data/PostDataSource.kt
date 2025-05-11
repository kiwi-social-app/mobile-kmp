package org.example.project.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import org.example.project.model.Post

class PostDataSource {

        private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private val postsUrl = "http://10.0.2.2:8080/posts/"

    suspend fun fetchPosts(): List<Post> {
        return client.get(postsUrl).body()
    }
}