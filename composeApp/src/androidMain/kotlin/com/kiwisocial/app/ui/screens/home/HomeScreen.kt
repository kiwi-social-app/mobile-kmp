package com.kiwisocial.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kiwisocial.app.model.Post
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiwisocial.app.viewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    currentUserId: String,
            onPostClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit
) {
    val posts by homeViewModel.displayedPosts.collectAsStateWithLifecycle()
    var showCreatePostDialog by remember { mutableStateOf(false) }
    val query by homeViewModel.searchQuery.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
           homeViewModel.fetchPosts()
    }

Scaffold(
    topBar = {
        TopAppBar(title = { Text("Home") })
    },
    floatingActionButton = {
        FloatingActionButton(onClick = { showCreatePostDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Create Post")
        }
    }
) {
    paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = homeViewModel::onQueryChange,
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                posts.isEmpty() && query.isBlank() -> CircularProgressIndicator()
                posts.isEmpty() -> Text("No results")
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts) { post ->
                        PostItem(
                            post = post,
                            onClick = { onPostClick(post.id) },
                            onAuthorClick = { onAuthorClick(post.author.id) },
                            currentUserId = currentUserId,
                            viewModel = homeViewModel
                        )
                    }
                }
            }
        }
    }
    if(showCreatePostDialog){
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onConfirm = { content ->
                homeViewModel.createPost(content)
                showCreatePostDialog = false
            }
        )
    }
}





}

@Composable
fun PostItem(
    post: Post,
    onClick: () -> Unit,
    onAuthorClick: () -> Unit,
    currentUserId: String?,
    viewModel: HomeViewModel
){
    val isLiked = currentUserId != null && post.likedByUsers.contains(currentUserId)
    val isDisliked = currentUserId != null && post.dislikedByUsers.contains(currentUserId)
    val isSaved = currentUserId != null && post.favoritedBy.contains(currentUserId)

    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }){
        Column(modifier = Modifier.padding(16.dp)){
            post.author.username?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onAuthorClick() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.body)
            Row {
                if(isLiked){
                    IconButton(onClick = { viewModel.removeLike(post.id) }) {
                        Icon(Icons.Filled.ThumbUp, contentDescription = "Like", tint = Color.Green)
                    }
                } else {
                    IconButton(onClick = { viewModel.addLike(post.id) }) {
                        Icon(Icons.Outlined.ThumbUp, contentDescription = "Like")
                    }
                }

                if(isDisliked){
                    IconButton(onClick = { viewModel.removeDislike(post.id) }) {
                        Icon(Icons.Filled.ThumbDown, contentDescription = "Dislike", tint = Color.Red)
                    }
                } else {
                    IconButton(onClick = { viewModel.addDislike(post.id) }) {
                        Icon(Icons.Outlined.ThumbDown, contentDescription = "Dislike")
                    }
                }

                if(isSaved){
                    IconButton(onClick = { viewModel.unFavoritePost(post.id) }) {
                        Icon(Icons.Filled.Bookmark, contentDescription = "Save", tint = Color.Blue)
                    }
                } else {
                    IconButton(onClick = { viewModel.favoritePost(post.id) }) {
                        Icon(Icons.Outlined.Bookmark, contentDescription = "Save")
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit){
    var content by remember {mutableStateOf("")}
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create a new post") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(content) },
                enabled = content.isNotBlank()
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )}
