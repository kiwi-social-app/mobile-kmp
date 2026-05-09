import SwiftUI
import shared
import FirebaseAuth

struct SearchView: View {
    private let viewModel = SearchViewModel()
    
    private var currentUserId: String? {
        Auth.auth().currentUser?.uid
    }
    
    var body: some View {
        Observing(viewModel.searchQuery, viewModel.results, viewModel.isLoading) { query, results, isLoading in
            VStack(spacing: 0) {
                TextField("Search", text: Binding(
                    get: {query},
                    set: { viewModel.onQueryChange(q: $0) }
                ))
                .textFieldStyle(.roundedBorder)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .padding()
                
                if isLoading.boolValue {
                    ProgressView().progressViewStyle(.linear)
                }
                
                Group {
                    if query.isEmpty {
                        ContentUnavailableView(
                            "Search posts",
                            systemImage: "magnifyingglass",
                            description: Text("Type to discover posts.")
                        )
                    } else if results.isEmpty && !isLoading.boolValue {
                        ContentUnavailableView(
                            "No results",
                            systemImage: "questionmark.circle",
                            description: Text("Try a different query.")
                        )
                    } else {
                        List(results, id: \.id) { post in
                            NavigationLink(destination: PostDetailView(postId: post.id)){
                                PostItemView(
                                    post: post,
                                    currentUserId: currentUserId,
                                    interactor: viewModel.interactions
                                )
                                .listRowSeparator(.hidden)
                                .listRowInsets(
                                    EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16)
                                )
                                .listRowBackground(Color.clear)
                            }
                        }
                        .listStyle(.plain)
                    }
                }
            }
            .navigationTitle("Search")
            
        }
    }
}

#Preview {
    SearchView()
}
