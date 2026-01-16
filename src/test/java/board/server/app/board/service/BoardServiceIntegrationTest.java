package board.server.app.board.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class BoardServiceIntegrationTest {
    @Autowired
    private BoardService boardService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;


    @Test
    void join() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .roleType(RoleType.MEMBER)
                .build();
        Board board = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();

        // insert query 1
        memberRepository.save(member);



        // WHEN
        // insert query 1
        Board user111 = boardService.join(board);


        // THEN
        Assertions.assertThat(user111.getMember().getId()).isEqualTo(member.getId());
        Assertions.assertThat(user111.getMember().getName()).isEqualTo(member.getName());
    }

    @Test
    void getBoard() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .roleType(RoleType.MEMBER)
                .build();
        Board board = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();

        // insert query 2
        memberRepository.save(member);
        boardRepository.save(board);


        // WHEN
        Board findBoard = boardService.getBoard(board.getId(), member.getName());



        // THEN
        // 조회수 증가 여부 확인
        Assertions.assertThat(findBoard.getViews()).isEqualTo(1L);
    }

    @Test
    void getBoardList() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .roleType(RoleType.MEMBER)
                .build();

        Board board1 = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();
        Board board2 = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();
        Board board3 = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();

        // insert query 4
        memberRepository.save(member);
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);



        // WHEN
        List<Board> boardList = boardService.getBoardList(member.getName());


        // THEN
        Assertions.assertThat(boardList.size()).isEqualTo(3);
    }

    @Test
    void getBoardListAll() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .roleType(RoleType.MEMBER)
                .build();


        // insert query 1
        memberRepository.save(member);

        // insert query 20
        for(var i=0; i<20; i++){
            Board board = Board.builder()
                    .views(0L)
                    .createdAt(LocalDateTime.now())
                    .contents("dasdas")
                    .title("dasdas")
                    .member(member)
                    .build();

            boardRepository.save(board);
        }


        // query 1
        List<Board> boardListAll1 = boardService.getBoardListAll(0);
        List<Board> boardListAll2 = boardService.getBoardListAll(1);

        Assertions.assertThat(boardListAll1.size()).isEqualTo(10);
        Assertions.assertThat(boardListAll2.size()).isEqualTo(10);


        for(int i=0; i<10; i++){
            Board board1 = boardListAll1.get(i);
            Board board2 = boardListAll2.get(i);

            Assertions.assertThat(board1.getId()).isNotEqualTo(board2.getId());
        }

    }

    @Test
    void setBoard() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .roleType(RoleType.MEMBER)
                .build();

        Board board = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();

        // insert query 2
        memberRepository.save(member);
        boardRepository.save(board);



        Board updateBoard = Board.builder()
                .title("hello")
                .contents("world")
                .member(member)
                .id(board.getId())
                .build();

        // WHEN
        boardService.setBoard(updateBoard, updateBoard.getId());
        

        // THEN
        Board isUpdatedBoard = boardRepository.findById(updateBoard.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND));


        Assertions.assertThat(updateBoard.getTitle()).isEqualTo(isUpdatedBoard.getTitle());
        Assertions.assertThat(updateBoard.getContents()).isEqualTo(isUpdatedBoard.getContents());
    }

    @Test
    void removeBoard() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .roleType(RoleType.MEMBER)
                .build();

        Board board = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();

        // insert query 2
        memberRepository.save(member);
        boardRepository.save(board);



        // WHEN
        boardService.removeBoard(member.getId(), board.getId());



        // THEN
        try {
            Board board1 = boardRepository.findById(board.getId()).orElseThrow(RuntimeException::new);
        }catch (RuntimeException e){
            return;
        }


        Assertions.fail("게시글이 존재함, 삭제처리되지않음");
    }
}