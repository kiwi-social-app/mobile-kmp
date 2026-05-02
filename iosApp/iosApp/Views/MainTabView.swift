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
                ChatListView(wsChatDataSource: wsChatDataSource)
            }
            .tabItem {
                Label("Chats", systemImage: "bubble.left")
            }

            NavigationStack {
                DashboardView(authRepository: authRepository)
            }
            .tabItem {
                Label("Account", systemImage: "person.crop.circle")
            }
        }
    }
}
