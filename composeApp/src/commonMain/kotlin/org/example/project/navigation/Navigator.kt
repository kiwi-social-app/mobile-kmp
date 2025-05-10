package org.example.project.navigation

interface Navigator {
    fun navigateTo(route: String)
    fun popBackStack()
}