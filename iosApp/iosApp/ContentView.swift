import SwiftUI
import shared

struct ContentView: View {
    @State private var isLoggedIn: Bool = false
    @State private var showContent = false
    var body: some View {
        if isLoggedIn {
                        HomeView()
                        .transition(.opacity)
                    } else {
                        LoginView(onLoginSuccess: {
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
        ContentView()
    }
}
