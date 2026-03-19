package board.server.app.boardTags.repository;

import board.server.app.boardTags.entity.BoardTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataJpaBoardTagsRepository extends JpaRepository<BoardTags, Long>, BoardTagsRepository {

    @Override
    @Query("select bt from BoardTags bt join fetch bt.tags t where bt.board.id = :boardId")
    List<BoardTags> findByBoard_IdWithTags(Long boardId);
}
