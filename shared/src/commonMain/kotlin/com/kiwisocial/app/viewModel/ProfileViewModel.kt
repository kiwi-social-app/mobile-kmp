package com.kiwisocial.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwisocial.app.data.PostDataSource
import com.kiwisocial.app.data.UserDataSource
import com.kiwisocial.app.model.Post
import com.kiwisocial.app.model.User
import com.kiwisocial.app.model.UserUpdate
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userId: String? = null) : ViewModel() {
    private val userDataSource = UserDataSource()
    private val postDataSource = PostDataSource()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isCurrentUser = MutableStateFlow(false)
    val isCurrentUser: StateFlow<Boolean> = _isCurrentUser.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _editUsername = MutableStateFlow("")
    val editUsername: StateFlow<String> = _editUsername.asStateFlow()

    private val _editEmail = MutableStateFlow("")
    val editEmail: StateFlow<String> = _editEmail.asStateFlow()

    private val _editFirstname = MutableStateFlow("")
    val editFirstname: StateFlow<String> = _editFirstname.asStateFlow()

    private val _editLastname = MutableStateFlow("")
    val editLastname: StateFlow<String> = _editLastname.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val currentUid = Firebase.auth.currentUser?.uid
        val effectiveId = userId ?: currentUid
        if (effectiveId == null) {
            println("ProfileViewModel: no user id available (not signed in?)")
            return
        }
        _isCurrentUser.value = (effectiveId == currentUid)

        viewModelScope.launch {
            try {
                _user.value = userDataSource.getUserById(effectiveId)
                _posts.value = postDataSource.getPostsByUser(effectiveId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startEditing() {
        val current = _user.value ?: return
        _editUsername.value = current.username ?: ""
        _editEmail.value = current.email
        _editFirstname.value = current.firstname ?: ""
        _editLastname.value = current.lastname ?: ""
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
    }

    fun onEditUsernameChange(value: String) {
        _editUsername.value = value
    }
    fun onEditEmailChange(value: String) {
        _editEmail.value = value
    }
    fun onEditFirstnameChange(value: String) {
        _editFirstname.value = value
    }
    fun onEditLastnameChange(value: String) {
        _editLastname.value = value
    }

    fun saveProfile() {
        val currentUid = Firebase.auth.currentUser?.uid
        val effectiveId = userId ?: currentUid ?: return

        viewModelScope.launch {
            try {
                val updated = userDataSource.updateUser(
                    effectiveId,
                    UserUpdate(
                        username = _editUsername.value.ifBlank { null },
                        email = _editEmail.value,
                        firstname = _editFirstname.value.ifBlank { null },
                        lastname = _editLastname.value.ifBlank { null },
                    ),
                )
                _user.value = updated
                _isEditing.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
