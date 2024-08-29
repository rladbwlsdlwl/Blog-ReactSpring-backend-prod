package board.server.app.board.repository;

import board.server.app.board.entity.Board;

import java.util.Optional;
import java.util.List;
public interface BoardRepository {
    Board save(Board board);
    Optional<Board> findByIdAndUsername(Long id, String username);
    List<Board> findByAuthor(Long author);
    Long update(Board board); // springdataJPA 에는 ORM에의해 find -> save
    void delete(Long id);
    List<Board> findAll();
}
