package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcTemplateBoardRepositoryTest {
    @Autowired JdbcTemplateBoardRepository jdbcTemplateBoardRepository;

    @Test
    void save() {
        // GIVEN
        // Board board = new Board("안녕", "안녕하세요", 35L);
        Board board = Board.builder()
                .title("hello")
                .contents("contents")
                .author(35L)
                .build();



        //WHEN
        Long id = jdbcTemplateBoardRepository.save(board).getId();

        // THEN
        Assertions.assertThat(id).isBetween(1L, 100L);

    }

    @Test
    void findById() throws SQLException {

        // GIVEN
        Board board1 = Board.builder()
                .title("hello")
                .contents("contents")
                .author(35L)
                .build();

        //WHEN
        Long id = jdbcTemplateBoardRepository.save(board1).getId();
        jdbcTemplateBoardRepository.findById(id).orElseThrow(() -> new SQLException("게시판 저장 or 게시판 탐색 에러"));

    }

    @Test
    void findByAuthor(){
        List<Board> byAuthor = jdbcTemplateBoardRepository.findByAuthor(38L);

        Assertions.assertThat(byAuthor.size()).isEqualTo(3);
    }
}