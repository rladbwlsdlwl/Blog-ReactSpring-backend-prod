package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.List;
public interface BoardRepository {
    Board save(Board board);
    Optional<Board> findById(Long id);
    List<Board> findByMember_name(String name);
    void deleteById(Long id);
    void delete(Board board);
    List<Board> findTop10ByOrderByCreatedAtDesc();
    List<Board> findTop10ByOrderByCreatedAtDescWithMember(Pageable pageable);
    Slice<Board> findByLessThanIdInitOrderByIdDescWithMember(Pageable pageable);
    Slice<Board> findByLessThanIdOrderByIdDescWithMember(Long lastId, Pageable pageable);
}
