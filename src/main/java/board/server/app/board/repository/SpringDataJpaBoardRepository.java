package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataJpaBoardRepository extends JpaRepository<Board, Long>, BoardRepository {
    @Override
    @Query("select b from Board b join fetch b.member order by b.createdAt desc")
    List<Board> findTop10ByOrderByCreatedAtDescWithMember(Pageable pageable);
}