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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
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

    @DisplayName("성공 - 30개 중 25개씩읽기")
    @Test
    void getBoardList() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .roleType(RoleType.MEMBER)
                .build();



        memberRepository.save(member);


        for(var i=0; i<30; i++){
            Board board = Board.builder()
                    .views(0L)
                    .createdAt(LocalDateTime.now())
                    .contents("dasdas")
                    .title("dasdas")
                    .member(member)
                    .build();


            boardRepository.save(board);
        }





        // WHEN
        int page = 0;

        Page<Board> boardPage = boardService.getBoardList(member.getName(), page);
        List<Board> boardList = boardPage.getContent();

        Long totalElement = boardPage.getTotalElements();
        int totalPages = boardPage.getTotalPages();


        // THEN
        // 25개 검증
        Assertions.assertThat(boardList.size()).isEqualTo(25);

        // 페이지 수 검증: 2
        Assertions.assertThat(totalPages).isEqualTo(2);

        // 총 게시글 길이 검증: 30
        Assertions.assertThat(totalElement).isEqualTo(30);

        // 내림차순 검증
        Assertions.assertThat(boardList).isSortedAccordingTo(Comparator.comparing(Board::getId).reversed());


        page++;

        boardPage = boardService.getBoardList(member.getName(), page);
        boardList = boardPage.getContent();


        // 5개 검증
        Assertions.assertThat(boardList.size()).isEqualTo(5);

        // 내림차순 검증
        Assertions.assertThat(boardList).isSortedAccordingTo(Comparator.comparing(Board::getId).reversed());
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

        // 게시글 20개 작성
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



        // WHEN
        List<Board> boardListAll1 = boardService.getBoardListAll(null).getContent();




        Long cursor = boardListAll1.get(boardListAll1.size() - 1).getId();
        List<Board> boardListAll2 = boardService.getBoardListAll(cursor).getContent();


        // 10개 페이징 검증
        Assertions.assertThat(boardListAll1.size()).isEqualTo(10);
        Assertions.assertThat(boardListAll2.size()).isEqualTo(10);


        // 리스트 독립된 값 검증
        Assertions.assertThat(boardListAll1.stream().map(Board::getId).toList()).doesNotHaveDuplicates();
        Assertions.assertThat(boardListAll2.stream().map(Board::getId).toList()).doesNotHaveDuplicates();



        // 리스트 간 중복 값 존재 검증
        Assertions.assertThat(boardListAll1).doesNotContainAnyElementsOf(boardListAll2);
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
        boardService.setBoard(board, updateBoard);
        

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