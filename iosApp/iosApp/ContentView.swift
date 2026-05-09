import SwiftUI
import shared

struct ContentView: View {
    @State private var isLoggedIn: Bool = false
    @State private var viewModel = AuthViewModel()

    let authRepository: AuthRepository
    let wsChatDataSource: WsChatDataSource
    
    var body: some View {
        Observing(viewModel.currentUser){ user in
            if user != nil {
                MainTabView(
                    authRepository: authRepository,
                    wsChatDataSource: wsChatDataSource,
                    onSignOut: {
                        Task { try? await wsChatDataSource.disconnect() }
                        viewModel.signOut()
                        withAnimation {
                            isLoggedIn = false
                        }
                    }
                )
                .transition(.opacity)
            } else {
                LoginView(
                    authRepository: authRepository,
                    onLoginSuccess: {
                    withAnimation {
                        isLoggedIn = true
                    }
                })
                .transition(.move(edge: .bottom))
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        Text("Preview unavailable for ContentView")
    }
}
