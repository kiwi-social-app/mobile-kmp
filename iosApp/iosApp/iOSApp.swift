import SwiftUI
import FirebaseCore
import shared

@main
struct iOSApp: App {
    let googleSignInProvider: GoogleSignInProvider
     let userDataSource: UserDataSource
     let authRepository: AuthRepository
     let wsChatDataSource: WsChatDataSource

    init() {
            FirebaseApp.configure()
        self.googleSignInProvider = GoogleSignInProvider()
        self.userDataSource = UserDataSource()
        self.authRepository = AuthRepository(
            googleSignInProvider: googleSignInProvider,
            userDataSource: userDataSource,
        )
        self.wsChatDataSource = WsChatDataSource()
        }
    
    var body: some Scene {
        WindowGroup {
            ContentView(
                authRepository: authRepository,
                wsChatDataSource: wsChatDataSource,
            )
        }
    }
}
