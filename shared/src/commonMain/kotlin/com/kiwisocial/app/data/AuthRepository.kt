package com.kiwisocial.app.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth

class AuthRepository(
    private val googleSignInProvider: GoogleSignInProvider,
    private val userDataSource: UserDataSource,
) {
    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        val result = Firebase.auth.signInWithEmailAndPassword(email, password)
        ensureBackendUserExists()
        return result
    }

    suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        val result = Firebase.auth.createUserWithEmailAndPassword(email, password)
        ensureBackendUserExists()
        return result
    }

    suspend fun signInWithGoogle(): AuthResult {
        val idToken = googleSignInProvider.getIdToken()
        val credential = GoogleAuthProvider.credential(idToken, null)
        val result = Firebase.auth.signInWithCredential(credential)
        ensureBackendUserExists()
        return result
    }

    suspend fun signOut() = Firebase.auth.signOut()

    private suspend fun ensureBackendUserExists() {
        userDataSource.getCurrentUser()
    }
}
