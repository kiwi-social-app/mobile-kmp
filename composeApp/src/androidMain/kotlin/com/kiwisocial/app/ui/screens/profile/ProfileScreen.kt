package com.kiwisocial.app.ui.screens.profile

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiwisocial.app.model.Post
import com.kiwisocial.app.model.User
import com.kiwisocial.app.viewModel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String? = null,
    onBack: (() -> Unit)? = null,
    onSignOut: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel(key = "profile-${userId ?: "me"}") {
        ProfileViewModel(userId = userId)
    }
) {
    val user by profileViewModel.user.collectAsStateWithLifecycle()
    val posts by profileViewModel.posts.collectAsStateWithLifecycle()
    val isCurrentUser by profileViewModel.isCurrentUser.collectAsStateWithLifecycle()
    val isEditing by profileViewModel.isEditing.collectAsStateWithLifecycle()
    val editUsername by profileViewModel.editUsername.collectAsStateWithLifecycle()
    val editEmail by profileViewModel.editEmail.collectAsStateWithLifecycle()
    val editFirstname by profileViewModel.editFirstname.collectAsStateWithLifecycle()
    val editLastname by profileViewModel.editLastname.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    if (onBack != null && userId != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (isCurrentUser) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "More options"
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit profile") },
                                    onClick = {
                                        showMenu = false
                                        profileViewModel.startEditing()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sign out") },
                                    onClick = {
                                        showMenu = false
                                        showSignOutDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentUser = user
        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    if (isEditing) {
                        EditUserInfoCard(
                            username = editUsername,
                            email = editEmail,
                            firstname = editFirstname,
                            lastname = editLastname,
                            onUsernameChange = profileViewModel::onEditUsernameChange,
                            onEmailChange = profileViewModel::onEditEmailChange,
                            onFirstnameChange = profileViewModel::onEditFirstnameChange,
                            onLastnameChange = profileViewModel::onEditLastnameChange,
                            onSave = profileViewModel::saveProfile,
                            onCancel = profileViewModel::cancelEditing
                        )
                    } else {
                        UserInfoCard(currentUser)
                    }
                }
                item {
                    Text(
                        text = "Posts",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (posts.isEmpty()) {
                    item {
                        Text(
                            text = "No posts yet",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(posts) { post ->
                        ProfilePostItem(post)
                    }
                }
            }
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign out?") },
            text = { Text("You'll need to sign in again to access your account.") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    onSignOut()
                }) { Text("Sign out") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserInfoCard(user: User) {
    val fullName = listOfNotNull(user.firstname, user.lastname)
        .joinToString(" ")
        .takeIf { it.isNotBlank() }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = user.username ?: "(no username)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            if (fullName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = fullName, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun EditUserInfoCard(
    username: String,
    email: String,
    firstname: String,
    lastname: String,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onFirstnameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = firstname,
                onValueChange = onFirstnameChange,
                label = { Text("First name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = lastname,
                onValueChange = onLastnameChange,
                label = { Text("Last name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSave,
                    enabled = email.isNotBlank()
                ) { Text("Save") }
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}

@Composable
private fun ProfilePostItem(post: Post) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.body)
        }
    }
}
