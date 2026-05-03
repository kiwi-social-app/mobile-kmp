package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.ChatDataSource
import com.kiwisocial.app.data.UserDataSource
import com.kiwisocial.app.model.Chat
import com.kiwisocial.app.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {
    private val chatDataSource = ChatDataSource()
    private val userDataSource = UserDataSource()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _availableUsers = MutableStateFlow<List<User>>(emptyList())
    val availableUsers: StateFlow<List<User>> = _availableUsers.asStateFlow()

    init {
        fetchChats()
        fetchAvailableUsers()
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

    fun fetchAvailableUsers() {
        viewModelScope.launch {
            try {
                val me = Firebase.auth.currentUser?.uid
                _availableUsers.value = userDataSource.getAllUsers()
                    .filter { it.id != me }
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
