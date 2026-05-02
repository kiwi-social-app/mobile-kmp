import SwiftUI
import shared

struct ChatListView: View {
    let wsChatDataSource: WsChatDataSource
    
    private let viewModel: ChatListViewModel

    init(wsChatDataSource: WsChatDataSource){
        self.viewModel = ChatListViewModel()
        self.wsChatDataSource = WsChatDataSource()
    }
    
    var body: some View {
        Text(/*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/)
    }
}

#Preview {
    ChatListView(wsChatDataSource: WsChatDataSource())
}
