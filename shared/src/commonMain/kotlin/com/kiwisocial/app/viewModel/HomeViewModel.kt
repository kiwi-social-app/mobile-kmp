package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.data.PostInteractionHandler
import com.kiwisocial.app.model.CreatePost
import com.kiwisocial.app.model.Post
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlin.collections.emptyList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val postDataSource = PostDataSource()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val interactions = PostInteractionHandler(viewModelScope, _posts, postDataSource)

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _posts.value = postDataSource.getAllPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPost(body: String) {
        viewModelScope.launch {
            try {
                val currentUser = Firebase.auth.currentUser
                if (currentUser == null) {
                    println("Error: User not authenticated")
                    return@launch
                }

                val newPost = postDataSource.createPost(
                    CreatePost(body = body),
                )
                _posts.value = listOf(newPost) + _posts.value
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
