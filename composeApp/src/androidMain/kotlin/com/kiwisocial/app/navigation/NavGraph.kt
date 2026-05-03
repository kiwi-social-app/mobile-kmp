package com.kiwisocial.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kiwisocial.app.data.AuthRepository
import com.kiwisocial.app.data.WsChatDataSource
import com.kiwisocial.app.ui.screens.chatDetail.ChatDetailScreen
import com.kiwisocial.app.ui.screens.chatList.ChatListScreen
import com.kiwisocial.app.ui.screens.home.HomeScreen
import com.kiwisocial.app.ui.screens.login.LoginScreen
import com.kiwisocial.app.ui.screens.postDetail.PostDetailScreen
import com.kiwisocial.app.ui.screens.profile.ProfileScreen
import com.kiwisocial.app.ui.screens.savedPosts.SavedPostsScreen
import com.kiwisocial.app.ui.screens.search.SearchScreen
import com.kiwisocial.app.ui.screens.signup.SignupScreen
import com.kiwisocial.app.viewModel.AuthViewModel
import com.kiwisocial.app.viewModel.ChatDetailViewModel
import com.kiwisocial.app.viewModel.ChatListViewModel
import com.kiwisocial.app.viewModel.LoginViewModel
import com.kiwisocial.app.viewModel.PostDetailViewModel
import com.kiwisocial.app.viewModel.SignupViewModel

@Composable
fun NavGraph(authRepository: AuthRepository, wsChatDataSource: WsChatDataSource) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val currentUserId = currentUser?.uid

    LaunchedEffect(currentUser, currentRoute) {
        if (currentUser == null) {
            wsChatDataSource.disconnect()
            if (currentRoute != null && currentRoute != "login" && currentRoute != "signup") {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        } else {
            try {
                wsChatDataSource.connect()
            } catch (_: Exception) {}
        }
    }

    Scaffold(bottomBar = {
        if (currentRoute != "login") {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEach { destination ->
                    val isSelected = currentRoute == destination.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.contentDescription) },
                        label = { Text(destination.label) },
                    )
                }
            }
        }
    }) { innerPadding ->
        NavHost(navController = navController, startDestination = "login", modifier = Modifier.padding(innerPadding)) {
            composable("login") {
                val loginViewModel: LoginViewModel = viewModel {
                    LoginViewModel(authRepository)
                }
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToSignup = { navController.navigate("signup") },
                    viewModel = loginViewModel,
                )
            }
            composable("home") {
                HomeScreen(
                    onPostClick = { postId ->
                        navController.navigate("post_details/$postId")
                    },
                    onAuthorClick = { authorId ->
                        navController.navigate("profile?userId=$authorId")
                    },
                    currentUserId = currentUserId ?: return@composable,
                )
            }
            composable("search") {
                SearchScreen()
            }
            composable("saved_posts") {
                SavedPostsScreen(
                    currentUserId = currentUserId ?: return@composable,
                    onPostClick = { postId ->
                        navController.navigate("post_details/$postId")
                    },
                )
            }
            composable("chat") {
                val chatListViewModel: ChatListViewModel = viewModel { ChatListViewModel() }
                ChatListScreen(
                    viewModel = chatListViewModel,
                    onChatClick = { chatId ->
                        navController.navigate("chat/$chatId")
                    },
                )
            }
            composable(
                route = "chat/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                val vm: ChatDetailViewModel = viewModel { ChatDetailViewModel(chatId, wsChatDataSource) }
                ChatDetailScreen(
                    viewModel = vm,
                    currentUserId = currentUserId ?: return@composable,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = "profile?userId={userId}",
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                ProfileScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onSignOut = { authViewModel.signOut() },
                )
            }
            composable(
                route = "post_details/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                val viewModel: PostDetailViewModel = viewModel {
                    PostDetailViewModel(postId = postId)
                }
                PostDetailScreen(
                    postDetailViewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAuthorClick = { authorId ->
                        navController.navigate("profile?userId=$authorId")
                    },
                    onPostDeleted = { navController.popBackStack() },
                )
            }
            composable("signup") {
                val signupViewModel: SignupViewModel = viewModel {
                    SignupViewModel(authRepository)
                }
                SignupScreen(
                    onNavigateToLogin = { navController.navigate("login") },
                    onSignupSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    viewModel = signupViewModel,
                )
            }
        }
    }
}
