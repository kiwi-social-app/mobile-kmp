package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.model.Post
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedPostsViewModel: ViewModel() {
    private val postDataSource = PostDataSource()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()
    private val currentUser = Firebase.auth.currentUser

    fun fetchSavedPosts() {
        viewModelScope.launch {
            try{
                _posts.value = postDataSource.getFavoritePosts()
            }
            catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun addLike(postId: String){
        val userId = currentUser?.uid ?: return

        viewModelScope.launch {
            try{
                postDataSource.addLike(postId)
                _posts.value = _posts.value.map {
                        post ->
                    if(post.id == postId) post.copy(likedByUsers = post.likedByUsers + userId, dislikedByUsers = post.dislikedByUsers - userId) else post
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeLike(postId: String){
        val userId = currentUser?.uid ?: return

        viewModelScope.launch {
            try{
                postDataSource.removeLike(postId)
                _posts.value = _posts.value.map {
                        post ->
                    if(post.id == postId) post.copy(likedByUsers = post.likedByUsers - userId) else post
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addDislike(postId: String){
        val userId = currentUser?.uid ?: return

        viewModelScope.launch {
            try{
                postDataSource.addDislike(postId)
                _posts.value = _posts.value.map {
                        post ->
                    if(post.id == postId) post.copy(likedByUsers = post.likedByUsers - userId, dislikedByUsers = post.dislikedByUsers + userId) else post
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeDislike(postId: String){
        val userId = currentUser?.uid ?: return

        viewModelScope.launch {
            try{
                postDataSource.removeDislike(postId)
                _posts.value = _posts.value.map {
                        post ->
                    if(post.id == postId) post.copy(dislikedByUsers = post.dislikedByUsers - userId) else post
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun favoritePost(postId: String){
        val userId = currentUser?.uid ?: return

        viewModelScope.launch {
            try{
                postDataSource.favoritePost(postId)
                _posts.value = _posts.value.map {
                        post ->
                    if(post.id == postId) post.copy(favoritedBy = post.favoritedBy + userId) else post
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unFavoritePost(postId: String){
        val userId = currentUser?.uid ?: return

        viewModelScope.launch {
            try{
                postDataSource.unFavoritePost(postId)
                _posts.value = _posts.value.map {
                        post ->
                    if(post.id == postId) post.copy(favoritedBy = post.favoritedBy - userId) else post
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}