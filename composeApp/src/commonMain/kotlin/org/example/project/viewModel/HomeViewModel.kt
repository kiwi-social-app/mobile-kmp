package org.example.project.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.example.project.data.PostDataSource
import org.example.project.model.CreatePost
import org.example.project.model.Post

class HomeViewModel: ViewModel() {
    private val postDataSource = PostDataSource()

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    fun fetchPosts(){
        viewModelScope.launch {
            try{
                posts = postDataSource.getAllPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createPost(body: String){
        viewModelScope.launch{
            try{
                val currentUser = Firebase.auth.currentUser
                if(currentUser === null){
                    println("Error: User not authenticated")
                    return@launch
                }

                val newPost = postDataSource.createPost(
                    userId=currentUser.uid,
                    CreatePost(body = body)
                )
            posts = listOf(newPost) + posts
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }
}