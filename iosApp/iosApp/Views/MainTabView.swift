import SwiftUI
import shared

struct MainTabView: View {
    let authRepository: AuthRepository
    let wsChatDataSource: WsChatDataSource
    let onSignOut: () -> Void

    var body: some View {
        TabView {
            NavigationStack {
                HomeView()
            }
            .tabItem {
                Label("Feed", systemImage: "house")
            }

            NavigationStack {
                SavedPostsView()
            }
            .tabItem {
                Label("Saved Posts", systemImage: "bookmark")
            }
            
            NavigationStack {
                ChatListView(wsChatDataSource: wsChatDataSource)
            }
            .tabItem {
                Label("Chats", systemImage: "bubble.left")
            }

            NavigationStack {
                SearchView()
            }
            .tabItem {
                Label("Search", systemImage: "magnifyingglass")
            }

            NavigationStack {
                ProfileView(onSignOut: onSignOut)
            }
            .tabItem {
                Label("Account", systemImage: "person.crop.circle")
            }
        }
        .task {
            do {
                try await wsChatDataSource.connect()
            } catch {
                print("WS connect failed: \(error)")
            }
        }
    }
}
