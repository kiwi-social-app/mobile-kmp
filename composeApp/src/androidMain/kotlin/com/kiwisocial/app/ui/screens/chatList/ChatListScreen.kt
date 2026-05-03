package com.kiwisocial.app.ui.screens.chatList

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiwisocial.app.model.User
import com.kiwisocial.app.viewModel.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(viewModel: ChatListViewModel, onChatClick: (String) -> Unit) {
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    var showStartNewChatDialog by remember { mutableStateOf(false) }
    val availableUsers by viewModel.availableUsers.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showStartNewChatDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Start new chat")
            }
        },
    ) { paddingValues ->
        if (chats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No chats yet")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(chats, key = { it.id }) { chat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChatClick(chat.id) },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = chat.participants.mapNotNull { it.username }.joinToString(", "),
                            fontWeight = FontWeight.Bold,
                        )
                        val last = chat.messages.lastOrNull()
                        if (last != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = last.content, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
    if (showStartNewChatDialog) {
        StartNewChatDialog(
            availableUsers = availableUsers,
            onDismiss = { showStartNewChatDialog = false },
            onConfirm = { content ->
                viewModel.startChat(content)
                showStartNewChatDialog = false
            },
        )
    }
}

@Composable
fun StartNewChatDialog(availableUsers: List<User>, onDismiss: () -> Unit, onConfirm: (List<String>) -> Unit) {
    var content by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "New Chat", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(availableUsers, key = { it.id }) { user ->
                    val checked = user.id in selectedIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (checked) {
                                    selectedIds.remove(user.id)
                                } else {
                                    selectedIds.add(user.id)
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = checked, onCheckedChange = null)
                        Spacer(Modifier.width(8.dp))
                        Text(user.username ?: user.id)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedIds.toList()) },
                enabled = selectedIds.isNotEmpty(),
            ) {
                Text("Start Chat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
