package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.data.PostInteractionHandler
import com.kiwisocial.app.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedPostsViewModel : ViewModel() {
    private val postDataSource = PostDataSource()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    val interactions = PostInteractionHandler(viewModelScope, _posts, postDataSource)

    fun fetchSavedPosts() {
        viewModelScope.launch {
            try {
                _posts.value = postDataSource.getFavoritePosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addLike(postId: String) = interactions.addLike(postId)
    fun removeLike(postId: String) = interactions.removeLike(postId)
    fun addDislike(postId: String) = interactions.addDislike(postId)
    fun removeDislike(postId: String) = interactions.removeDislike(postId)
    fun favoritePost(postId: String) = interactions.favoritePost(postId)
    fun unFavoritePost(postId: String) = interactions.unFavoritePost(postId)
}
