package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.ChatDataSource
import com.kiwisocial.app.model.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel: ViewModel() {
    private val chatDataSource = ChatDataSource()
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    init {
        fetchChats()
    }


    fun fetchChats() {
        viewModelScope.launch {
            try {
                _chats.value = chatDataSource.getCurrentUserChats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startChat(participantIds: List<String>) {
        viewModelScope.launch {
            try {
                chatDataSource.startChat(participantIds)
                fetchChats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}