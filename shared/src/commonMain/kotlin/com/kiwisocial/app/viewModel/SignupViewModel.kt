package com.kiwisocial.app.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.AuthRepository
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val authState = mutableStateOf<AuthState>(AuthState.Idle)

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = authRepository.signUpWithEmail(email, password)
                val user = result.user
                if (user != null) {
                    authState.value = AuthState.Success(user)
                    onSuccess()
                } else {
                    val msg = "Signup failed"
                    authState.value = AuthState.Error(msg)
                    _errorMessage.value = msg
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Signup failed"
                authState.value = AuthState.Error(msg)
                _errorMessage.value = msg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogle(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authRepository.signInWithGoogle()
                onSuccess()
            } catch (e: Exception) {
                val msg = e.message ?: "Google sign-in failed"
                _errorMessage.value = msg
                onError(msg)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}
