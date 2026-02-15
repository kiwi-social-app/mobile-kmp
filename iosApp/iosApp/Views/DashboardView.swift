import SwiftUI

struct DashboardView: View {
//    @StateObject var userViewModel = UserViewModel()
//    @StateObject var authViewModel = AuthViewModel()
    
    var body: some View {
//        VStack(spacing: 16){
//            if let user = userViewModel.user{
//                Text("Welcome, \(user.username)")
//                    .font(.largeTitle)
//                    .bold()
//                Text("Email: \(user.email)")
//                
//                if user.firstname != nil {
//                    Text("First Name: \(user.firstname ?? "")")
//                }
//                if user.lastname != nil {
//                    Text("Last Name: \(user.lastname ?? "")")
//                }
//                
//            } else if let error = userViewModel.errorMessage {
//                Text("Error: \(error)")
//                    .foregroundColor(.red)
//            } else {
//                ProgressView("Loading user info...")
//            }
//            
//            Button(action: {
//                authViewModel.signOut()
//            }){
//                Text("Sign out")
//            }
//        }
//        .padding()
//        .navigationTitle("Your Account")
//        .onAppear {
//               userViewModel.fetchUser()
//           }
    }
}

#Preview {
    DashboardView()
}
