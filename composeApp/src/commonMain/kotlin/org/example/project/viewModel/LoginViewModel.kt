package org.example.project.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth

import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password)
                if (result.user != null) {
                    onSuccess()
                } else {
                    onError("Invalid credentials")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            } finally {
                isLoading = false
            }
        }
    }
}
