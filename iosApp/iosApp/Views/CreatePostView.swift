import SwiftUI

struct CreatePostView: View {
    @State private var content: String = ""
    var onDismiss: () -> Void
    var onConfirm: (String) -> Void

    var body: some View {
        NavigationStack {
            Form {
                Section(header: Text("What's on your mind?")) {
                    TextEditor(text: $content)
                        .frame(minHeight: 150)
                }
            }
            .navigationTitle("New Post")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel", action: onDismiss)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Post") {
                        onConfirm(content)
                    }
                    .disabled(content.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }
            }
        }
    }
}
