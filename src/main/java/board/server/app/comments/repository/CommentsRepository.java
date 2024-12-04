package board.server.app.comments.repository;


import board.server.app.comments.entity.Comments;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository {
    Optional<Comments> findById(Long id);
    List<Comments> findByBoard_IdInWithMemberOrderByCreatedAtAsc(List<Long> idList);
    Comments save(Comments comments);
    void deleteById(Long id);
    void delete(Comments comments);
}
