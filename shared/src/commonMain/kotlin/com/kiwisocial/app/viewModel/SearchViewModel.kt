package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.data.PostInteractionHandler
import com.kiwisocial.app.data.SearchDataSource
import com.kiwisocial.app.model.Post
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

class SearchViewModel : ViewModel() {
    private val searchDataSource = SearchDataSource()
    private val postDataSource = PostDataSource()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _results = MutableStateFlow<List<Post>>(emptyList())
    val results: StateFlow<List<Post>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val interactions = PostInteractionHandler(viewModelScope, _results, postDataSource)

    init {
        _searchQuery
            .onEach { _isLoading.value = it.isNotBlank() }
            .debounce(300.milliseconds)
            .distinctUntilChanged()
            .mapLatest { query ->
                if (query.isBlank()) {
                    emptyList()
                } else {
                    try {
                        val hits = searchDataSource.search(query)
                        val postIds = hits.mapNotNull { it.postId }.distinct()
                        coroutineScope {
                            postIds.map { id ->
                                async {
                                    try {
                                        postDataSource.getPostById(id)
                                    } catch (
                                        e: Exception,
                                    ) {
                                        e.printStackTrace()
                                        null
                                    }
                                }
                            }.awaitAll().filterNotNull()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList()
                    }
                }
            }
            .onEach {
                _results.value = it
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(q: String) {
        _searchQuery.value = q
    }
}
