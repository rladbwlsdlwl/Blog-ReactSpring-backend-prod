package board.server.app.board.repository;

import board.server.app.board.entity.Board;

import java.util.Optional;
import java.util.List;
public interface BoardRepository {
    Board save(Board board);
    Optional<Board> findById(Long id);
    List<Board> findByMember_name(String name);
    Long update(Board board); // springdataJPA 에는 변경감지 setter OR save(내부적으로 merge, 비영속 오버헤드 발생)
    void deleteById(Long id);
    void delete(Board board);
    List<Board> findTop10ByOrderByCreatedAtDesc();
}
