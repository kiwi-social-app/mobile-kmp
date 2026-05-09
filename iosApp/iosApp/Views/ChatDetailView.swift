import SwiftUI
import shared
import FirebaseAuth

struct ChatDetailView: View {
    @State private var viewModel: ChatDetailViewModel
    private let currentUserId: String?
    @State private var draft: String = ""

    init(chatId: String, wsChatDataSource: WsChatDataSource) {
        _viewModel = State(initialValue: ChatDetailViewModel(
            chatId: chatId,
            wsChatDataSource: wsChatDataSource
        ))
        self.currentUserId = Auth.auth().currentUser?.uid
    }

    var body: some View {
        Observing(viewModel.messages) { messages in
            ScrollViewReader { proxy in
                List(messages, id: \.id) { message in
                    MessageBubble(
                        message: message,
                        isMine: message.sender.id == currentUserId
                    )
                    .listRowSeparator(.hidden)
                    .listRowBackground(Color.clear)
                    .id(message.id)
                }
                .listStyle(.plain)
                .onChange(of: messages.count) { _, _ in
                    if let last = messages.last {
                        withAnimation {
                            proxy.scrollTo(last.id, anchor: .bottom)
                        }
                    }
                }
            }
            .navigationTitle("Chat")
            .navigationBarTitleDisplayMode(.inline)
            .safeAreaInset(edge: .bottom) {
                composer
            }
        }
    }

private var composer: some View {
    HStack(spacing: 8) {
        TextField("Message", text: $draft, axis: .vertical)
            .textFieldStyle(.roundedBorder)
            .lineLimit(1...4)
        Button {
            let trimmed = draft.trimmingCharacters(in: .whitespacesAndNewlines)
            guard !trimmed.isEmpty else { return }
            viewModel.sendMessage(content: trimmed)
            draft = ""
        } label: {
            Image(systemName: "paperplane.fill")
                .font(.title3)
        }
        .disabled(draft.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
    }
    .padding(.horizontal)
    .padding(.vertical, 8)
    .background(.bar)
}
}

private struct MessageBubble: View {
    let message: Message
    let isMine: Bool

    var body: some View {
        HStack {
            if isMine { Spacer(minLength: 40) }
            Text(message.content)
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(
                    isMine
                        ? Color.accentColor.opacity(0.2)
                        : Color(.systemGray5)
                )
                .foregroundColor(.primary)
                .cornerRadius(12)
            if !isMine { Spacer(minLength: 40) }
        }
    }
}

#Preview {
    ChatDetailView(chatId:"", wsChatDataSource: WsChatDataSource())
}

