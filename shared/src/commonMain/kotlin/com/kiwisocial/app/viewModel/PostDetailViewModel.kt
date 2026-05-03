package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.CommentDataSource
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.model.Comment
import com.kiwisocial.app.model.CreateComment
import com.kiwisocial.app.model.Post
import com.kiwisocial.app.model.UpdatePost
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlin.collections.emptyList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PostDetailEvent {
    data object PostDeleted : PostDetailEvent()
}

sealed class PostDetailState {
    data object Loading : PostDetailState()
    data class Success(
        val post: Post,
        val comments: List<Comment> = emptyList(),
        val currentUser: FirebaseUser? = Firebase.auth.currentUser,
        val isEditing: Boolean = false,
    ) : PostDetailState()
    data class Error(val message: String) : PostDetailState()
}

class PostDetailViewModel(
    private val postId: String,
    private val postDataSource: PostDataSource = PostDataSource(),
    private val commentDataSource: CommentDataSource = CommentDataSource(),
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PostDetailEvent>()
    val events: SharedFlow<PostDetailEvent> = _events.asSharedFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                val post = postDataSource.getPostById(postId)
                val comments = commentDataSource.getCommentsByPostId(postId)
                _uiState.value = PostDetailState.Success(post, comments)
            } catch (e: Exception) {
                _uiState.value = PostDetailState.Error(e.message ?: "Unknown Error")
                e.printStackTrace()
            }
        }
    }

    fun createComment(body: String) {
        val currentState = _uiState.value
        if (currentState !is PostDetailState.Success) return

        viewModelScope.launch {
            try {
                val currentUser = Firebase.auth.currentUser
                if (currentUser == null) {
                    println("Error: User not authenticated")
                    return@launch
                }

                val newComment = commentDataSource.createComment(
                    currentState.post.id,
                    CreateComment(body = body),
                )
                _uiState.value = currentState.copy(
                    comments = listOf<Comment>(newComment) + currentState.comments,
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setEditing(editing: Boolean) {
        val currentState = _uiState.value
        if (currentState is PostDetailState.Success) {
            _uiState.value = currentState.copy(isEditing = editing)
        }
    }

    fun updatePost(newBody: String) {
        val currentState = _uiState.value
        if (currentState !is PostDetailState.Success) return

        viewModelScope.launch {
            try {
                val updated = postDataSource.updatePost(postId, UpdatePost(body = newBody))
                _uiState.value = currentState.copy(post = updated, isEditing = false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            try {
                postDataSource.deletePost(postId)
                _events.emit(PostDetailEvent.PostDeleted)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
