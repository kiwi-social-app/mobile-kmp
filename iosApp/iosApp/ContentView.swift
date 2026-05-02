import SwiftUI
import shared

struct ContentView: View {
    @State private var isLoggedIn: Bool = false
    @State private var authViewModel = AuthViewModel()

    let authRepository: AuthRepository
    let wsChatDataSource: WsChatDataSource
    
    var body: some View {
        if isLoggedIn {
                        MainTabView(
                            authRepository: authRepository,
                            wsChatDataSource: wsChatDataSource,
                            onSignOut: { authViewModel.signOut() }
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

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        Text("Preview unavailable for ContentView")
    }
}
