package com.kiwisocial.app.ui.screens.chatList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiwisocial.app.viewModel.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    onChatClick: (String) -> Unit,
    ) {
    val chats by viewModel.chats.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") },

            )
        }
    ) { paddingValues ->
        if(chats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No chats yet")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(chats, key = {it.id}){ chat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChatClick(chat.id) }
                ){
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = chat.participants.mapNotNull { it.username }.joinToString(", "),
                            fontWeight = FontWeight.Bold,
                        )
                        val last = chat.messages.lastOrNull()
                        if(last != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = last.content, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
