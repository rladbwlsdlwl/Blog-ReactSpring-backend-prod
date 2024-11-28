package board.server.app.likes.repository;

import board.server.app.likes.entity.Likes;

import java.util.List;
import java.util.Optional;

public interface LikesRepository {
    List<Likes> findByBoard_Id(Long id);
    List<Likes> findByBoard_IdIn(List<Long> idList);
    Optional<Likes> findByBoard_IdAndMember_Id(Long boardId, Long memberId);
    Likes save(Likes likes);
    void delete(Likes likes);
    void deleteById(Long id);
}
