import FirebaseAuth
import SwiftUI
import shared

struct SavedPostsView: View {
    private let viewModel = SavedPostsViewModel()

    private var currentUserId: String? {
        Auth.auth().currentUser?.uid
    }

    var body: some View {
        Observing(viewModel.posts) { posts in
            Group {
                if posts.isEmpty {
                    ProgressView()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    List(posts, id: \.id) { post in
                        NavigationLink(
                            destination: PostDetailView(postId: post.id)
                        ) {
                            PostItemView(
                                post: post,
                                currentUserId: currentUserId,
                                interactor: viewModel.interactions
                            )
                            .listRowSeparator(.hidden)
                            .listRowInsets(
                                EdgeInsets(
                                    top: 8,
                                    leading: 16,
                                    bottom: 8,
                                    trailing: 16
                                )
                            )
                            .listRowBackground(Color.clear)
                        }
                    }
                    .listStyle(.plain)
                    .refreshable{
                        viewModel.fetchSavedPosts()
                    }
                }
            }
            .navigationTitle("Saved")
        }
        .onAppear{
            viewModel.fetchSavedPosts()
        }
    }
}

#Preview {
    SavedPostsView()
}
