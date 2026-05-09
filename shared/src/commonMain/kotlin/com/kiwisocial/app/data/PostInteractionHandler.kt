package com.kiwisocial.app.data

import com.kiwisocial.app.model.Post
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PostInteractionHandler(
    private val scope: CoroutineScope,

    private val posts: MutableStateFlow<List<Post>>,
    private val postDataSource: PostDataSource = PostDataSource(),
) {
    private val currentUserId: String? get() = Firebase.auth.currentUser?.uid

    fun addLike(postId: String) {
        val userId = currentUserId ?: return

        scope.launch {
            try {
                postDataSource.addLike(postId)
                posts.value = posts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            likedByUsers = post.likedByUsers + userId,
                            dislikedByUsers =
                            post.dislikedByUsers - userId,
                        )
                    } else {
                        post
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeLike(postId: String) {
        val userId = currentUserId ?: return
        scope.launch {
            try {
                postDataSource.removeLike(postId)
                posts.value = posts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(likedByUsers = post.likedByUsers - userId)
                    } else {
                        post
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addDislike(postId: String) {
        val userId = currentUserId ?: return
        scope.launch {
            try {
                postDataSource.addDislike(postId)
                posts.value = posts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            likedByUsers = post.likedByUsers - userId,
                            dislikedByUsers = post.dislikedByUsers + userId,
                        )
                    } else {
                        post
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeDislike(postId: String) {
        val userId = currentUserId ?: return
        scope.launch {
            try {
                postDataSource.removeDislike(postId)
                posts.value = posts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(dislikedByUsers = post.dislikedByUsers - userId)
                    } else {
                        post
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun favoritePost(postId: String) {
        val userId = currentUserId ?: return
        scope.launch {
            try {
                postDataSource.favoritePost(postId)
                posts.value = posts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(favoritedBy = post.favoritedBy + userId)
                    } else {
                        post
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unFavoritePost(postId: String) {
        val userId = currentUserId ?: return
        scope.launch {
            try {
                postDataSource.unFavoritePost(postId)
                posts.value = posts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(favoritedBy = post.favoritedBy - userId)
                    } else {
                        post
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
