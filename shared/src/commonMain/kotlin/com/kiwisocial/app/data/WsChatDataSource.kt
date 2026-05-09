package com.kiwisocial.app.data

import com.kiwisocial.app.model.Message
import com.kiwisocial.app.model.OutgoingMessage
import com.kiwisocial.app.wsUrl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withTextConversions
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient

class WsChatDataSource {
    private val httpClient = HttpClient {
        install(WebSockets)
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED }

    private val json = Json { ignoreUnknownKeys = true }
    private val client = StompClient(KtorWebSocketClient(httpClient))

    private var session: StompSessionWithKxSerialization? = null

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    suspend fun connect() {
        if (session != null) return
        _connectionState.value = ConnectionState.CONNECTING
        try {
            val token = Firebase.auth.currentUser?.getIdToken(false)
            val rawSession = client.connect(
                url = wsUrl,
                customStompConnectHeaders = token
                    ?.let { mapOf("Authorization" to "Bearer $it") }
                    ?: emptyMap(),
            )
            session = rawSession.withTextConversions(json, "application/json;charset=utf-8")
            _connectionState.value = ConnectionState.CONNECTED
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.DISCONNECTED
            e.printStackTrace()
            throw e
        }
    }

    suspend fun subscribeToChat(chatId: String): Flow<Message> {
        val s = session ?: error("Call connect() before subscribing")
        try {
            return s.subscribe("/topic/chats/$chatId", Message.serializer())
        } catch (e: Exception) {
            session = null
            _connectionState.value = ConnectionState.DISCONNECTED
            throw e
        }
    }

    suspend fun sendMessage(message: OutgoingMessage) {
        val s = session ?: error("Call connect() before sending")
        try {
            s.convertAndSend("/app/sendMessage", message, OutgoingMessage.serializer())
        } catch (e: Exception) {
            session = null
            _connectionState.value = ConnectionState.DISCONNECTED
            throw e
        }
    }

    suspend fun disconnect() {
        session?.disconnect()
        session = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}
