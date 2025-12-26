import SwiftUI
import shared

struct HomeView: View {
    private let viewModel = HomeViewModel()
    @State private var showCreatePostDialog = false

    var body: some View {
        Observing(viewModel.posts) { posts in
            NavigationStack {
                ZStack {
                    if posts.isEmpty {
                        ProgressView("Loading posts...")
                    } else {
                        List(posts, id: \.id) { post in
                            PostItemView(post: post)
                                .listRowSeparator(Visibility.hidden)
                                .listRowInsets(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
                                .listRowBackground(Color.clear)
                        }
                        .listStyle(.plain)
                    }
                }
                .navigationTitle("Kiwi Social")
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
