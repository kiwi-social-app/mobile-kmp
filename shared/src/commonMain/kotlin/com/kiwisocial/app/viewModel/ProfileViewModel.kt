package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.data.UserDataSource
import com.kiwisocial.app.model.Post
import com.kiwisocial.app.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userId: String? = null) : ViewModel() {
    private val userDataSource = UserDataSource()
    private val postDataSource = PostDataSource()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isCurrentUser = MutableStateFlow(false)
    val isCurrentUser: StateFlow<Boolean> = _isCurrentUser.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val currentUid = Firebase.auth.currentUser?.uid
        val effectiveId = userId ?: currentUid
        if (effectiveId == null) {
            println("ProfileViewModel: no user id available (not signed in?)")
            return
        }
        _isCurrentUser.value = (effectiveId == currentUid)

        viewModelScope.launch {
            try {
                _user.value = userDataSource.getUserById(effectiveId)
                _posts.value = postDataSource.getPostsByUser(effectiveId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
