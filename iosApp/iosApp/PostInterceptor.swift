import shared

protocol PostInteractor {
    func addLike(postId: String)
    func removeLike(postId: String)
    func addDislike(postId: String)
    func removeDislike(postId: String)
    func favoritePost(postId: String)
    func unFavoritePost(postId: String)
}

extension HomeViewModel: PostInteractor {}
extension SavedPostsViewModel: PostInteractor {}
