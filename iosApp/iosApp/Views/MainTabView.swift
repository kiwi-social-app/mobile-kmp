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

            NavigationStack{
                SearchView()
            }
            .tabItem {
                Label("Search", systemImage: "search")
            }
            
            NavigationStack {
                ChatListView(wsChatDataSource: wsChatDataSource)
            }
            .tabItem {
                Label("Chats", systemImage: "bubble.left")
            }
            
            NavigationStack {
                SavedPostsView()
            }
            .tabItem {
                Label("Saved Posts", systemImage: "bookmark")
            }

            NavigationStack {
                ProfileView(authRepository: authRepository)
            }
            .tabItem {
                Label("Account", systemImage: "person.crop.circle")
            }
        }
    }
}
