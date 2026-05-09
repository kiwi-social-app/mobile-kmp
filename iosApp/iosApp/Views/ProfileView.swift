import SwiftUI
import shared

struct ProfileView: View {
    let onSignOut: () -> Void
    private let viewModel: ProfileViewModel
    @State private var showSignOutDialog = false

    init(userId: String? = nil, onSignOut: @escaping () -> Void = {}) {
        self.viewModel = ProfileViewModel(userId: userId)
        self.onSignOut = {}
    }

    var body: some View {
        Observing(
            viewModel.user,
            viewModel.posts,
            viewModel.isCurrentUser,
            viewModel.isEditing
        ) { user, posts, isCurrentUser, isEditing in
            Group {
                if let user = user {
                    VStack(alignment: .leading, spacing: 8) {
                        if !isEditing.boolValue {
                            userInfo(user: user)
                        } else {
                            editForm()
                        }

                    }
                    .padding()
                } else {
                    ProgressView()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                }

            }
            .navigationTitle("User Profile")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    if isCurrentUser.boolValue {
                        Menu {
                            Button {
                                viewModel.startEditing()
                            } label: {
                                Label("Edit profile", systemImage: "pencil")
                            }
                            Button(role: .destructive) {
                                showSignOutDialog = true
                            } label: {
                                Label(
                                    "Sign out",
                                    systemImage:
                                        "rectangle.portrait.and.arrow.right"
                                )
                            }
                        } label: {
                            Image(systemName: "ellipsis.circle")
                        }
                    }
                }
            }
            .confirmationDialog(
                "Sign out?",
                isPresented: $showSignOutDialog,
                titleVisibility: .visible
            ) {
                Button("Sign out", role: .destructive, action: onSignOut)
                Button("Cancel", role: .cancel) {}
            } message: {
                Text("You'll need to sign in again to access your account.")
            }

        }
    }

    private func editForm() -> some View {
        Observing(
            viewModel.editUsername,
            viewModel.editEmail,
            viewModel.editFirstname,
            viewModel.editLastname,
        ) { username, email, firstname, lastname in
            VStack(alignment: .leading, spacing: 12) {
                TextField(
                    "Username",
                    text: Binding(
                        get: { username },
                        set: { viewModel.onEditUsernameChange(value: $0) }
                    )
                )
                .textFieldStyle(.roundedBorder)
                .textInputAutocapitalization(.never)

                TextField(
                    "Email",
                    text: Binding(
                        get: { email },
                        set: { viewModel.onEditEmailChange(value: $0) }
                    )
                )
                .textFieldStyle(.roundedBorder)
                .keyboardType(.emailAddress)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()

                TextField(
                    "First name",
                    text: Binding(
                        get: { firstname },
                        set: { viewModel.onEditFirstnameChange(value: $0) }
                    )
                )
                .textFieldStyle(.roundedBorder)

                TextField(
                    "Last name",
                    text: Binding(
                        get: { lastname },
                        set: { viewModel.onEditLastnameChange(value: $0) }
                    )
                )
                .textFieldStyle(.roundedBorder)

                HStack(spacing: 8) {
                    Button("Save") {
                        viewModel.saveProfile()
                    }
                    .buttonStyle(.borderedProminent)
                    .disabled(
                        email.trimmingCharacters(in: .whitespacesAndNewlines)
                            .isEmpty
                    )

                    Button("Cancel") {
                        viewModel.cancelEditing()
                    }
                    .buttonStyle(.bordered)
                }

            }
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
    }

    private func userInfo(user: User) -> some View {
        VStack {
            Text(user.email)
            Text(user.username ?? "Unknown")
            Text(user.firstname ?? "Unknown")
            Text(user.lastname ?? "Unknown")
        }
    }

}

#Preview {
    ProfileView(
        onSignOut: {}
    )
}
