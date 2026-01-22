package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaBoardRepository extends JpaRepository<Board, Long>, BoardRepository {
    // 10개 단순조회 (createdAt)
    @Override
    @Query("select b from Board b join fetch b.member order by b.createdAt desc")
    List<Board> findTop10ByOrderByCreatedAtDesc();
    // 이동 가능한 버튼식 (id)
    // Offset
    @Override
    @Query("select b from Board b where b.member.id = :memberId order by b.id desc")
    Page<Board> findAllByMember_IdOrderByIdDesc(@Param("memberId") Long id, Pageable pageable);

    // 무한 스크롤 (id)
    // No Offset
    @Override
    @Query("select b from Board b join fetch b.member order by b.id desc")
    Slice<Board> findByLessThanIdInitOrderByIdDescWithMember(Pageable pageable);

    // 무한 스크롤 (id)
    // No Offset
    @Override
    @Query("select b from Board b join fetch b.member where b.id < :lastId order by b.id desc")
    Slice<Board> findByLessThanIdOrderByIdDescWithMember(@Param("lastId") Long lastId, Pageable pageable);
}