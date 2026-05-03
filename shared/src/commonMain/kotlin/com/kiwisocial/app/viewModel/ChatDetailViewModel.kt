package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.ChatDataSource
import com.kiwisocial.app.data.WsChatDataSource
import com.kiwisocial.app.model.Message
import com.kiwisocial.app.model.OutgoingMessage
import com.kiwisocial.app.model.SenderRef
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlin.collections.emptyList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatDetailViewModel(private val chatId: String, private val wsChatDataSource: WsChatDataSource) : ViewModel() {
    private val chatDataSource = ChatDataSource()

    val messages: StateFlow<List<Message>> = flow {
        val history = try {
            chatDataSource.getMessagesByChatId(chatId)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
            return@flow
        }
        emit(history)
        try {
            wsChatDataSource.subscribeToChat(chatId)
                .scan(history) { acc, m ->
                    if (acc.any { it.id == m.id }) acc else acc + m
                }
                .collect { emit(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendMessage(content: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                wsChatDataSource.sendMessage(
                    OutgoingMessage(chatId, SenderRef(userId), content),
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
