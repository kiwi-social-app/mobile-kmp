package com.kiwisocial.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME("home", "Home", Icons.Default.Home, "Home"),
    CHAT("chat", "Chat", Icons.Default.ChatBubble, "Chat"),
    PROFILE("profile", "Profile", Icons.Default.Person, "Profile"),
    SAVED_POSTS("saved_posts", "Saved", Icons.Default.Bookmark, "SavedPosts"),
}