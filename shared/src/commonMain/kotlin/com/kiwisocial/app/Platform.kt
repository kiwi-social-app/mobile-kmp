package com.kiwisocial.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect val baseUrl: String