package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.CommentDataSource
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.model.Comment
import com.kiwisocial.app.model.CreateComment
import com.kiwisocial.app.model.Post
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

sealed class PostDetailState {
    data object Loading : PostDetailState()
    data class Success(
        val post: Post,
        val comments: List<Comment> = emptyList(),
        val currentUser: FirebaseUser? = Firebase.auth.currentUser
    ) : PostDetailState()
    data class Error(val message: String) : PostDetailState()
}

class PostDetailViewModel(
    private val postId: String,
    private val postDataSource: PostDataSource = PostDataSource(),
    private val commentDataSource: CommentDataSource = CommentDataSource()
): ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try{
                val post = postDataSource.getPostById(postId)
                val comments = commentDataSource.getCommentsByPostId(postId)
                _uiState.value = PostDetailState.Success(post, comments)
            } catch(e: Exception){
                _uiState.value = PostDetailState.Error(e.message ?: "Unknown Error")
                e.printStackTrace()
            }
        }
    }

    fun createComment(body: String){
        val currentState = _uiState.value
        if (currentState !is PostDetailState.Success) return

        viewModelScope.launch {
            try{
                val currentUser = Firebase.auth.currentUser
                if(currentUser == null){
                    println("Error: User not authenticated")
                    return@launch
                }

                val newComment = commentDataSource.createComment(
                    currentState.post.id,
                    CreateComment(body = body)
                )
                _uiState.value = currentState.copy(
                    comments = listOf<Comment>(newComment) + currentState.comments
                )            }
            catch(e: Exception){
                e.printStackTrace()
            }
        }
    }
}