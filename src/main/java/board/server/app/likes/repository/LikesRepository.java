package board.server.app.likes.repository;

import board.server.app.likes.entity.Likes;

import java.util.List;
import java.util.Optional;

public interface LikesRepository {
    public List<Likes> findByPostId(Long postId);
    public Optional<Likes> findByPostIdAndAuthor(Long postId, Long author);
    public Likes save(Likes likes);
    public void delete(Likes likes);
}
