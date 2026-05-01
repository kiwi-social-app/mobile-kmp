package com.kiwisocial.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect val baseUrl: String

val wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://") + "/api/ws"
