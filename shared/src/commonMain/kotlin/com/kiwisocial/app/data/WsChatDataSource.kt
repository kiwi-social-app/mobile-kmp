package com.kiwisocial.app.data

import com.kiwisocial.app.model.Message
import com.kiwisocial.app.model.OutgoingMessage
import com.kiwisocial.app.wsUrl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
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
    enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED }

    private val json = Json { ignoreUnknownKeys = true }
    private val client = StompClient(KtorWebSocketClient())

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
        return s.subscribe("/topic/chats/$chatId", Message.serializer())
    }

    suspend fun sendMessage(message: OutgoingMessage) {
        val s = session ?: error("Call connect() before sending")
        s.convertAndSend("/app/sendMessage", message, OutgoingMessage.serializer())
    }

    suspend fun disconnect() {
        session?.disconnect()
        session = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}
