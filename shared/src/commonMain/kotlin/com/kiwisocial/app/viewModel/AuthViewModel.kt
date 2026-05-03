package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> =
        Firebase.auth.authStateChanged.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Firebase.auth.currentUser,
        )

    fun signOut() {
        viewModelScope.launch {
            try {
                Firebase.auth.signOut()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
