import SwiftUI
import shared

struct PostItemView: View {
    let post: Post
    let currentUserId: String?
    let interactor: any PostInteractor

    private var isLiked: Bool {
        currentUserId.map { post.likedByUsers.contains($0) } ?? false
    }
    private var isDisliked: Bool {
        currentUserId.map { post.dislikedByUsers.contains($0) } ?? false
    }
    private var isSaved: Bool {
        currentUserId.map { post.favoritedBy.contains($0) } ?? false
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(post.author.username ?? "Unknown User")
                .font(.headline)

            Text(post.body)
                .font(.body)
            HStack(spacing: 16) {
                Button {
                    isLiked
                        ? interactor.removeLike(postId: post.id)
                        : interactor.addLike(postId: post.id)
                } label: {
                    Image(
                        systemName: isLiked
                            ? "hand.thumbsup.fill" : "hand.thumbsup"
                    )
                    .foregroundColor(isLiked ? .green : .secondary)
                }
                Button {
                    isDisliked
                        ? interactor.removeDislike(postId: post.id)
                        : interactor.addDislike(postId: post.id)
                } label: {
                    Image(
                        systemName: isDisliked
                            ? "hand.thumbsdown.fill" : "hand.thumbsdown"
                    )
                    .foregroundColor(isDisliked ? .red : .secondary)
                }
                Button {
                    isSaved
                        ? interactor.unFavoritePost(postId: post.id)
                        : interactor.favoritePost(postId: post.id)
                } label: {
                    Image(systemName: isSaved ? "bookmark.fill" : "bookmark")
                        .foregroundColor(isSaved ? .blue : .secondary)
                }
            }
            .buttonStyle(.plain)
            .padding(.top, 4)
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}
