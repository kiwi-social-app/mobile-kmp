package com.kiwisocial.app.data

import com.kiwisocial.app.baseUrl
import com.kiwisocial.app.model.GoogleAuthUser
import com.kiwisocial.app.model.User
import com.kiwisocial.app.model.UserUpdate
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
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class UserDataSource {
    private val client = HttpClient {
        expectSuccess = true
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

    private val userUrl = "$baseUrl/api/users"

    private suspend fun getAuthToken(): String? {
        return Firebase.auth.currentUser?.getIdToken(false)
    }

    suspend fun getUserById(id: String): User {
        return client.get("$userUrl/$id") {
            getAuthToken()?.let { bearerAuth(it) }
        }.body()
    }

    suspend fun createUser(uid: String, email: String): User {
        return client.post(userUrl) {
            contentType(ContentType.Application.Json)
            getAuthToken()?.let { bearerAuth(it) }
            setBody(GoogleAuthUser(uid = uid, email = email))
        }.body()
    }

    suspend fun updateUser(userId: String, userUpdate: UserUpdate): User {
        return client.put("$userUrl/$userId") {
            contentType(ContentType.Application.Json)
            getAuthToken()?.let { bearerAuth(it) }
            setBody(userUpdate)
        }.body()
    }
}
