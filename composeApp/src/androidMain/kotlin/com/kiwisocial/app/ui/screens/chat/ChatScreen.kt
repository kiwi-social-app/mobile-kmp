package com.kiwisocial.app.ui.screens.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import java.lang.reflect.Modifier

@Composable
fun ChatScreen() {
    Scaffold { paddingValues ->
        Box(modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ){}
    }
}