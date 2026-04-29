package com.kiwisocial.app.data

expect class GoogleSignInProvider {
    suspend fun getIdToken(): String
}