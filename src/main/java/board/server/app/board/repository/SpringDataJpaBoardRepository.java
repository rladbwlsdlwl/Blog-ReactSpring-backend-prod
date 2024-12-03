package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaBoardRepository extends JpaRepository<Board, Long>, BoardRepository {

}