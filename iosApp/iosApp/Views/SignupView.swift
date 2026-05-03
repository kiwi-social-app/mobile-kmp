import SwiftUI
import shared

struct SignupView: View {
    var onSignupSuccess: () -> Void
    private let viewModel: SignupViewModel
    
    init(authRepository: AuthRepository, onSignupSuccess: @escaping () -> Void){
        self.onSignupSuccess = onSignupSuccess
        self.viewModel = SignupViewModel(authRepository: authRepository)
    }
    
    var body: some View {
        Observing(viewModel.email, viewModel.password, viewModel.isLoading, viewModel.errorMessage){
            email, password, isLoading, errorMessage in VStack(spacing: 16){
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

                if (isLoading as? Bool) == true {
                                    ProgressView()
                                }
                if let errorMessage { Text(errorMessage).foregroundColor(.red) }

                                Button("Sign up") {
                                    viewModel.signUp(
                                        email: email,
                                        password: password,
                                        onSuccess: onSignupSuccess,
                                    )
                                }
                                .disabled((isLoading as? Bool) == true)
            }
            .padding()
        }
    }
}

#Preview {
    SignupView(
        authRepository: AuthRepository(
            googleSignInProvider: GoogleSignInProvider(),
                          userDataSource: UserDataSource()
        ),
        onSignupSuccess:  {}
    )
}
