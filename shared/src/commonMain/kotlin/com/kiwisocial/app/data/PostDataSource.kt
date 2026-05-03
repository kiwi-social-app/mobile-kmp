package com.kiwisocial.app.data

import com.kiwisocial.app.baseUrl
import com.kiwisocial.app.model.CreatePost
import com.kiwisocial.app.model.Post
import com.kiwisocial.app.model.UpdatePost
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
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PostDataSource {

    private val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    private val postsUrl = "$baseUrl/api/posts"

    private suspend fun getAuthToken(): String? = Firebase.auth.currentUser?.getIdToken(false)

    suspend fun getAllPosts(): List<Post> = client.get(postsUrl) {
        getAuthToken()?.let { bearerAuth(it) }
    }.body()

    suspend fun getFavoritePosts(): List<Post> = client.get("$postsUrl/favorites") {
        getAuthToken()?.let { bearerAuth(it) }
    }.body()

    suspend fun createPost(createPost: CreatePost): Post = client.post(postsUrl) {
        contentType(ContentType.Application.Json)
        getAuthToken()?.let { bearerAuth(it) }
        setBody(createPost)
    }.body()

    suspend fun getPostById(postId: String): Post = client.get("$postsUrl/$postId") {
        getAuthToken()?.let { bearerAuth(it) }
    }.body()

    suspend fun updatePost(postId: String, update: UpdatePost): Post = client.put("$postsUrl/$postId") {
        contentType(ContentType.Application.Json)
        getAuthToken()?.let { bearerAuth(it) }
        setBody(update)
    }.body()

    suspend fun deletePost(postId: String) {
        client.delete("$postsUrl/$postId") {
            getAuthToken()?.let { bearerAuth(it) }
        }
    }

    suspend fun getCurrentUserPosts(): List<Post> = client.get("$postsUrl/mine") {
        getAuthToken()?.let { bearerAuth(it) }
    }.body()

    suspend fun getPostsByUser(userId: String): List<Post> = client.get("$postsUrl/user/$userId") {
        getAuthToken()?.let { bearerAuth(it) }
    }.body()

    suspend fun addLike(postId: String) {
        client.post("$postsUrl/$postId/like") {
            getAuthToken()?.let { bearerAuth(it) }
        }
    }

    suspend fun removeLike(postId: String) {
        client.delete("$postsUrl/$postId/like") {
            getAuthToken()?.let { bearerAuth(it) }
        }
    }

    suspend fun addDislike(postId: String) {
        client.post("$postsUrl/$postId/dislike") {
            getAuthToken()?.let { bearerAuth(it) }
        }
    }

    suspend fun removeDislike(postId: String) {
        client.delete("$postsUrl/$postId/dislike") {
            getAuthToken()?.let { bearerAuth(it) }
        }
    }

    suspend fun favoritePost(postId: String) {
        client.post("$postsUrl/$postId/favorite") {
            getAuthToken()?.let { bearerAuth(it) }
        }
    }

    suspend fun unFavoritePost(postId: String) {
        client.delete("$postsUrl/$postId/favorite") {
            getAuthToken()?.let { bearerAuth(it) }
        }
    }
}
