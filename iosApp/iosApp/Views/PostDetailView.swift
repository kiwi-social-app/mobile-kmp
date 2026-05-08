import SwiftUI
import shared

struct PostDetailView: View {
    private let viewModel: PostDetailViewModel
    @State private var commentDraft: String = ""

    init(
        postId: String,
    ) {
        self.viewModel = PostDetailViewModel(
            postId: postId,
            postDataSource: PostDataSource(),
            commentDataSource: CommentDataSource()
        )
    }

    var body: some View {
        Observing(viewModel.uiState) { state in
            content(for: state)
        }
        .navigationTitle("Post")
        .navigationBarTitleDisplayMode(.inline)
    }

    @ViewBuilder
    private func content(for state: PostDetailState) -> some View {
        if state is PostDetailState.Loading {
            ProgressView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if let success = state as? PostDetailState.Success {
            successBody(success)
        } else if let error = state as? PostDetailState.Error {
            Text(error.message)
                .foregroundColor(.red)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }

    private func successBody(_ state: PostDetailState.Success) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                postCard(state.post)
                Divider()
                Text("\(state.comments.count) Comments")
                    .font(.subheadline.weight(.semibold))
                CommentsView(comments: state.comments)
            }.padding()
        }.safeAreaInset(edge: .bottom) {
            commentComposer
        }
    }

    private func postCard(_ post: Post) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(post.author.username ?? "Unknown")
                .font(.headline)
            Text(post.body)
                .font(.body)
            Text(post.createdAt)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
    }

    private var commentComposer: some View {
        HStack(spacing: 8) {
            TextField("Add a comment…", text: $commentDraft, axis: .vertical)
                .textFieldStyle(.roundedBorder)
                .lineLimit(1...3)
            Button {
                let body = commentDraft.trimmingCharacters(
                    in: .whitespacesAndNewlines
                )
                guard !body.isEmpty else { return }
                viewModel.createComment(body: body)
                commentDraft = ""
            } label: {
                Image(systemName: "paperplane.fill")
                    .font(.title3)
            }
            .disabled(
                commentDraft.trimmingCharacters(in: .whitespacesAndNewlines)
                    .isEmpty
            )
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(.bar)
    }
}
