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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kiwisocial.app.viewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
            onPostClick: (String) -> Unit
) {
    val posts by homeViewModel.posts.collectAsStateWithLifecycle()
    var showCreatePostDialog by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
           homeViewModel.fetchPosts()
    }

Scaffold(
    floatingActionButton = {
        FloatingActionButton(onClick = { showCreatePostDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Create Post")
        }
    }
) {
    paddingValues ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ){
        if(posts.isEmpty()){
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                items(posts) { post ->
                    PostItem(post = post, onClick = { onPostClick(post.id) })
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
fun PostItem(post: Post, onClick: () -> Unit){
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }){
        Column(modifier = Modifier.padding(16.dp)){
            post.author.username?.let { Text(text = it, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.body)
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
