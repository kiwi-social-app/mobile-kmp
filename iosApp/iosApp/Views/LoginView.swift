import SwiftUI
import shared


struct LoginView: View {
    var onLoginSuccess: () -> Void
    private let viewModel: LoginViewModel
    
    
    init(authRepository: AuthRepository, onLoginSuccess: @escaping () -> Void) {
        self.onLoginSuccess = onLoginSuccess
        self.viewModel = LoginViewModel(authRepository: authRepository)
    }
              
    
    var body: some View {
        Observing(viewModel.email, viewModel.password, viewModel.isLoading){
            email, password, isLoading in VStack(spacing: 16){
                TextField("Email", text: Binding(
                                    get: { email },
                                    set: { viewModel.onEmailChange(newEmail: $0) }
                                ))
                                .textFieldStyle(.roundedBorder)

                                SecureField("Password", text: Binding(
                                    get: { password },
                                    set: { viewModel.onPasswordChange(newPassword: $0) }
                                ))
                                .textFieldStyle(.roundedBorder)

                if isLoading.boolValue {
                                    ProgressView()
                                }

                                Button("Login") {
                                    viewModel.login(onSuccess: onLoginSuccess) { error in
                                        print(error)
                                    }
                                }
                                .disabled(isLoading.boolValue)
            }
            .padding()
        }    }
}

#Preview {
    LoginView(
              authRepository: AuthRepository(
        googleSignInProvider: GoogleSignInProvider(),
                      userDataSource: UserDataSource()
                  ),
              onLoginSuccess: {},
    )
}
