package com.kiwisocial.app.data

import com.kiwisocial.app.baseUrl
import com.kiwisocial.app.model.SearchResult
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SearchDataSource {

    private val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }
    private val searchUrl = "$baseUrl/api/search"

    private suspend fun getAuthToken(): String? = Firebase.auth.currentUser?.getIdToken(false)

    suspend fun search(query: String): List<SearchResult> = client.get(searchUrl) {
        getAuthToken()?.let { bearerAuth(it) }
        parameter("query", query)
    }.body()
}
