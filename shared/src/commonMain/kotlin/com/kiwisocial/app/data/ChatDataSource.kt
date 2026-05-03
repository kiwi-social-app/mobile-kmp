package com.kiwisocial.app.data

import com.kiwisocial.app.baseUrl
import com.kiwisocial.app.model.Chat
import com.kiwisocial.app.model.Message
import com.kiwisocial.app.model.StartChatRequest
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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ChatDataSource {
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

    private val chatUrl = "$baseUrl/api/chat"

    private suspend fun getAuthToken(): String? = Firebase.auth.currentUser?.getIdToken(false)

    suspend fun getCurrentUserChats(): List<Chat> = client.get("$chatUrl/user/me") {
        getAuthToken()?.let { bearerAuth(it) }
    }.body()

    suspend fun startChat(participantIds: List<String>): Chat = client.post("$chatUrl/start") {
        contentType(ContentType.Application.Json)
        getAuthToken()?.let { bearerAuth(it) }
        setBody(StartChatRequest(participantIds))
    }.body()

    suspend fun getMessagesByChatId(id: String): List<Message> = client.get("$chatUrl/messages/$id") {
        getAuthToken()?.let { bearerAuth(it) }
    }.body()
}
