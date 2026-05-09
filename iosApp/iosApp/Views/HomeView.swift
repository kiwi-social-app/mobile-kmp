import SwiftUI
import shared
import FirebaseAuth

struct HomeView: View {
    @State private var viewModel = HomeViewModel()
    @State private var showCreatePostDialog = false
    
    private var currentUserId: String? {
        Auth.auth().currentUser?.uid
    }

    var body: some View {
        Observing(viewModel.posts, viewModel.isLoading) { posts, isLoading in
            NavigationStack {
                Group {
                    if(isLoading.boolValue){
                        ProgressView("Loading posts...")
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } else if posts.isEmpty {
                        Text("No posts yet").frame(maxWidth: .infinity, maxHeight: .infinity)
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
                        .refreshable {
                            viewModel.fetchPosts()
                        }
                    }
                }
                .navigationTitle("Feed")
                .toolbar {
                    ToolbarItem(placement: .primaryAction) {
                        Button(action: { showCreatePostDialog = true }) {
                            Image(systemName: "plus.circle.fill")
                                .font(.title2)
                        }
                    }
                }
                .sheet(isPresented: $showCreatePostDialog) {
                    CreatePostView(
                        onDismiss: { showCreatePostDialog = false },
                        onConfirm: { content in
                            viewModel.createPost(body: content)
                            showCreatePostDialog = false
                        }
                    )
                }
            }
        }
        .onAppear {
            viewModel.fetchPosts()
        }
    }
}
