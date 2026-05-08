import SwiftUI
import shared

struct CommentsView: View {
    let comments: [Comment]

    var body: some View {
        if comments.isEmpty {
            Text("Be the first to leave a comment!")
                .foregroundColor(.gray)
                .italic()
                .frame(maxWidth: .infinity, alignment: .leading)
        } else {
            VStack(alignment: .leading, spacing: 8) {
                ForEach(comments, id: \.id) { comment in
                    CommentRow(comment: comment)
                }
            }
        }
    }
}

private struct CommentRow: View {
    let comment: Comment

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text(comment.author.username ?? "Unknown")
                    .font(.subheadline.weight(.semibold))
                Spacer()
                Text(comment.createdAt)
                    .font(.caption)
                    .foregroundColor(.gray)
            }
            Text(comment.body)
                .font(.body)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(10)
        .shadow(color: Color.black.opacity(0.06), radius: 1, x: 0, y: 1)
    }
}
