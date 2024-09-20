package board.server.app.comments.repository;


import board.server.app.comments.entity.Comments;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository {
    Optional<Comments> findById(Long id);
    List<Comments> findByBoardId(Long boardId);
    Long save(Comments comments);
    void delete(Long id);
}
