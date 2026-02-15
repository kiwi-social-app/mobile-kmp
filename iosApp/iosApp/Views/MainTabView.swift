import SwiftUI

struct MainTabView: View {
    var body: some View {
        TabView {
            NavigationStack {
                HomeView()
            }
            .tabItem {
                Label("Feed", systemImage: "house")
            }

            NavigationStack {
                ChatView()
            }
            .tabItem {
                Label("Chats", systemImage: "bubble.left")
            }

            NavigationStack {
                DashboardView()
            }
            .tabItem {
                Label("Account", systemImage: "person.crop.circle")
            }
        }
    }
}
