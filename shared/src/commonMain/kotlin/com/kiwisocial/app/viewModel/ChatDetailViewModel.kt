package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.ChatDataSource
import com.kiwisocial.app.data.WsChatDataSource
import com.kiwisocial.app.model.Chat
import com.kiwisocial.app.model.Message
import com.kiwisocial.app.model.OutgoingMessage
import com.kiwisocial.app.model.SenderRef
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class ChatDetailViewModel(
    private val chatId: String,
    private val wsChatDataSource: WsChatDataSource
): ViewModel(
) {
    private val chatDataSource = ChatDataSource()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()

    val messages: StateFlow<List<Message>> = flow {
        val history = chatDataSource.getMessagesByChatId(chatId)
        emit(history)
        try {
            wsChatDataSource.subscribeToChat(chatId)
                .scan(history) { acc, m ->
                    if (acc.any { it.id == m.id }) acc else acc + m
                }
                .collect { emit(it) }
        } catch (e: Exception) { e.printStackTrace() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendMessage(content: String) {
        val chatId = _selectedChatId.value ?: return
        val userId = Firebase.auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                wsChatDataSource.sendMessage(
                    OutgoingMessage(chatId, SenderRef(userId), content)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}