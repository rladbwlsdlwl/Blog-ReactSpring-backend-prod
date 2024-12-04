package board.server.app.comments.repository;

import board.server.app.comments.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaCommentsRepository extends JpaRepository<Comments, Long>, CommentsRepository {
    @Override
    @Query("select c from Comments c join fetch c.member where c.board.id in :boardIdList order by createdAt asc")
    List<Comments> findByBoard_IdInWithMemberOrderByCreatedAtAsc(@Param("boardIdList") List<Long> idList);
}
