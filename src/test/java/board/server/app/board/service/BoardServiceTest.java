package board.server.app.board.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.JdbcTemplateBoardRepository;
import board.server.app.user.entity.User;
import board.server.app.user.repository.JdbcTemplateUserRepository;
import board.server.app.user.service.UserService;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@SpringBootTest
@Transactional
class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    UserService userService;
    @Autowired
    JdbcTemplateUserRepository jdbcTemplateUserRepository;
    @Autowired
    JdbcTemplateBoardRepository jdbcTemplateBoardRepository;

    @Test
    void getBoard() throws Exception{
        // GIVEN
        User user = new User("user123", "user123@aaaa.aaa", "user1111");
        Long author = userService.join(user);

        Board board1 = Board.builder()
                .title("hello")
                .contents("contents")
                .author(author)
                .build();

        // when
        Long join = boardService.join(board1, "user123");
        Board board = boardService.getBoard(join, "user123");

        // THEN
        Assertions.assertEquals(join, board.getId());
    }

    @Test
    void getBoardsByAuthor(){
        // GIVEN
        User user = new User("user123", "user123@aaaa.aaa", "user1111");
        Long author = userService.join(user);

        Board board = Board.builder()
                .author(author)
                .title("zzzz")
                .contents("zzzz??")
                .build();
        Long board_id = boardService.join(board, "user123");


        // WHEN
//        Optional<User> user123 = jdbcTemplateUserRepository.findByUsername("user123");
//        user123.orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.USER_NO_PERMISSION));
//
//        User user1 = user123.get();

        User user123 = jdbcTemplateUserRepository.findByUsername("user123").orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.USER_NO_PERMISSION));


        Assertions.assertEquals(user123.getId(), author, "cannot save");

        List<Board> author123 = jdbcTemplateBoardRepository.findByAuthor(user123.getId());
        Assertions.assertEquals(author123.size(), 1, "cannot read");
    }
}