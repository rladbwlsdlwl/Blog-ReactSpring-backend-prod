package board.server.app.board.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.JdbcTemplateBoardRepository;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.app.member.service.MemberService;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;
    @Autowired
    JdbcTemplateMemberRepository jdbcTemplateMemberRepository;
    @Autowired
    JdbcTemplateBoardRepository jdbcTemplateBoardRepository;

    @Test
    void getBoard() throws Exception{
        // GIVEN
        Member member = new Member("user123", "user123@aaaa.aaa", "user1111");
        Long author = memberService.join(member);

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
        Member member = new Member("user123", "user123@aaaa.aaa", "user1111");
        Long author = memberService.join(member);

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

        Member member123 = jdbcTemplateMemberRepository.findByName("user123").orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION));


        Assertions.assertEquals(member123.getId(), author, "cannot save");

        List<Board> author123 = jdbcTemplateBoardRepository.findByAuthor(member123.getId());
        Assertions.assertEquals(author123.size(), 1, "cannot read");
    }
}