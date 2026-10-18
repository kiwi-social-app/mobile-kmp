package com.kiwisocial.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

object ApiConfig {
    lateinit var baseUrl: String
}

val baseUrl: String get() = ApiConfig.baseUrl

val wsUrl: String get() = baseUrl.replace("http://", "ws://").replace("https://", "wss://") + "/api/ws"
