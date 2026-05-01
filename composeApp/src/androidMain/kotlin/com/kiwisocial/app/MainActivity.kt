package com.kiwisocial.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.kiwisocial.app.data.AuthRepository
import com.kiwisocial.app.data.GoogleSignInProvider
import com.kiwisocial.app.data.UserDataSource
import com.kiwisocial.app.data.WsChatDataSource
import com.kiwisocial.app.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleSignInProvider = GoogleSignInProvider(
            activityContext = this,
            webClientId = getString(R.string.default_web_client_id),
        )
        val userDataSource = UserDataSource()
        val authRepository = AuthRepository(googleSignInProvider, userDataSource)
        val wsChatDataSource = WsChatDataSource()

        setContent {
            MaterialTheme {
                NavGraph(authRepository = authRepository, wsChatDataSource = wsChatDataSource)
            }
        }
    }
}
