package com.kiwisocial.app.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun ProfileScreen(){
    Scaffold { paddingValues ->
        Box(modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(paddingValues),
            contentAlignment = Alignment.Center
        ){}
    }
}