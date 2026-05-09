import SwiftUI
import shared

struct ChatListView: View {
    let wsChatDataSource: WsChatDataSource
    @State private var viewModel = ChatListViewModel()
    @State private var showStartNewChat = false

    var body: some View {
        Observing(viewModel.chats) { chats in
            Group {
                if chats.isEmpty {
                    ContentUnavailableView(
                        "No chats yet",
                        systemImage: "bubble.left",
                        description: Text(
                            "Start a new chat to begin messaging."
                        )
                    )
                } else {
                    List(chats, id: \.id) { chat in
                        NavigationLink {
                            ChatDetailView(
                                chatId: chat.id,
                                wsChatDataSource: wsChatDataSource
                            )
                        } label: {
                            ChatRow(chat: chat)
                        }
                    }
                    .listStyle(.plain)
                }
            }
            .navigationTitle("Chats")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showStartNewChat = true
                    } label: {
                        Image(systemName: "plus.circle.fill")
                            .font(.title2)
                    }
                }
            }
            .sheet(isPresented: $showStartNewChat) {
                StartNewChatSheet(
                    viewModel: viewModel,
                    onDismiss: {
                        showStartNewChat = false
                    }
                )
            }
        }
    }
}

private struct ChatRow: View {
    let chat: Chat

    private var participantNames: String {
        chat.participants.compactMap { $0.username }.joined(separator: ", ")
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(participantNames.isEmpty ? "Chat" : participantNames).font(
                .headline
            )
            if let last = chat.messages.last {
                Text(last.content)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
            }
        }
        .padding(.vertical, 4)
    }
}

private struct StartNewChatSheet: View {
    let viewModel: ChatListViewModel
    let onDismiss: () -> Void
    @State private var selectedIds: Set<String> = []

    var body: some View {
        NavigationStack {
            Observing(viewModel.availableUsers) { users in
                List(users, id: \.id) { user in
                    HStack {
                        Image(
                            systemName: selectedIds.contains(user.id)
                                ? "checkmark.square.fill"
                                : "square"
                        )
                        .foregroundColor(
                            selectedIds.contains(user.id)
                                ? .accentColor : .secondary
                        )
                        Text(user.username ?? user.id)
                    }
                    .contentShape(Rectangle())
                    .onTapGesture {
                        if selectedIds.contains(user.id) {
                            selectedIds.remove(user.id)
                        } else {
                            selectedIds.insert(user.id)
                        }
                    }
                }
                .navigationTitle("New Chat")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button("Cancel", action: onDismiss)
                    }
                    ToolbarItem(placement: .topBarTrailing) {
                        Button("Start") {
                            viewModel.startChat(
                                participantIds: Array(selectedIds)
                            )
                            onDismiss()
                        }
                        .disabled(selectedIds.isEmpty)
                    }
                }
            }
        }
    }
}

#Preview {
    ChatListView(wsChatDataSource: WsChatDataSource())
}
