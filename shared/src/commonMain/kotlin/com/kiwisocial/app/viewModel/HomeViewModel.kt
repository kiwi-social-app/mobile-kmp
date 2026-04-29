package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.data.SearchDataSource
import com.kiwisocial.app.model.CreatePost
import com.kiwisocial.app.model.Post
import com.kiwisocial.app.model.SearchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.emptyList
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel: ViewModel() {
    private val postDataSource = PostDataSource()
    private val searchDataSource = SearchDataSource()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()
    private val currentUser = Firebase.auth.currentUser

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<SearchResult>> = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .mapLatest { query ->
            if(query.isBlank()){
                emptyList()
            } else {
                try {
                    searchDataSource.search(query)
                } catch(e: Exception){
                    e.printStackTrace()
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val displayedPosts: StateFlow<List<Post>> = combine(_posts, searchResults) { posts, results ->
        if (results.isEmpty()) {
            posts
        } else {
            val byId = posts.associateBy { it.id }
            results.mapNotNull { it.postId?.let(byId::get) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onQueryChange(q: String) {
        _searchQuery.value = q
    }

    fun fetchPosts(){
        viewModelScope.launch {
            try{
                _posts.value = postDataSource.getAllPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createPost(body: String){
        viewModelScope.launch{
            try{
                val currentUser = Firebase.auth.currentUser
                if(currentUser == null){
                    println("Error: User not authenticated")
                    return@launch
                }

                val newPost = postDataSource.createPost(
                    CreatePost(body = body)
                )
                _posts.value = listOf(newPost) + _posts.value
            }
            catch(e:Exception){
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