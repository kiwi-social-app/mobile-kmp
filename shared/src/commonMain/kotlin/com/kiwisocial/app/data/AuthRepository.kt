package com.kiwisocial.app.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode

class AuthRepository(
    private val googleSignInProvider: GoogleSignInProvider,
    private val userDataSource: UserDataSource,
) {
    suspend fun signInWithEmail(email: String, password: String) =
        Firebase.auth.signInWithEmailAndPassword(email, password)

    suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        val result = Firebase.auth.createUserWithEmailAndPassword(email, password)
        ensureBackendUserExists(result, fallbackEmail = email)
        return result
    }

    suspend fun signInWithGoogle(): AuthResult {
        val idToken = googleSignInProvider.getIdToken()
        val credential = GoogleAuthProvider.credential(idToken, null)
        val result = Firebase.auth.signInWithCredential(credential)
        ensureBackendUserExists(result, fallbackEmail = null)
        return result
    }

    suspend fun signOut() = Firebase.auth.signOut()

    private suspend fun ensureBackendUserExists(result: AuthResult, fallbackEmail: String?) {
        val user = result.user ?: return
        val email = user.email ?: fallbackEmail
            ?: error("Cannot provision backend user without an email")

        val exists = try {
            userDataSource.getUserById(user.uid)
            true
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) false else throw e
        }

        if (!exists) {
            userDataSource.createUser(uid = user.uid, email = email)
        }
    }
}
