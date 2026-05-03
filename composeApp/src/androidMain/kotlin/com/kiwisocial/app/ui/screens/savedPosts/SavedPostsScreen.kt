package com.kiwisocial.app.ui.screens.savedPosts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiwisocial.app.model.Post
import com.kiwisocial.app.viewModel.SavedPostsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPostsScreen(
    savedPostsViewModel: SavedPostsViewModel = viewModel(),
    currentUserId: String,
    onPostClick: (String) -> Unit,
) {
    val posts by savedPostsViewModel.posts.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        savedPostsViewModel.fetchSavedPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Saved Posts") })
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            if (posts.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(posts) { post ->
                        PostItem(post = post, onClick = {
                            onPostClick(post.id)
                        }, currentUserId = currentUserId, savedPostsViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun PostItem(post: Post, onClick: () -> Unit, currentUserId: String?, viewModel: SavedPostsViewModel) {
    val isLiked = currentUserId != null && post.likedByUsers.contains(currentUserId)
    val isDisliked = currentUserId != null && post.dislikedByUsers.contains(currentUserId)

    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            post.author.username?.let { Text(text = it, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.body)
            Row {
                if (isLiked) {
                    IconButton(onClick = { viewModel.removeLike(post.id) }) {
                        Icon(Icons.Filled.ThumbUp, contentDescription = "Like", tint = Color.Green)
                    }
                } else {
                    IconButton(onClick = { viewModel.addLike(post.id) }) {
                        Icon(Icons.Outlined.ThumbUp, contentDescription = "Like")
                    }
                }

                if (isDisliked) {
                    IconButton(onClick = { viewModel.removeDislike(post.id) }) {
                        Icon(Icons.Filled.ThumbDown, contentDescription = "Dislike", tint = Color.Red)
                    }
                } else {
                    IconButton(onClick = { viewModel.addDislike(post.id) }) {
                        Icon(Icons.Outlined.ThumbDown, contentDescription = "Dislike")
                    }
                }
            }
        }
    }
}
