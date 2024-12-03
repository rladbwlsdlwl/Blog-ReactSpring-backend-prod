package board.server.app.board.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardServiceTest {
    @Autowired
    private BoardService boardService;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager em;

    @Test
    void join() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .build();
        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
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
        roleRepository.save(role);

        em.flush();
        em.clear();



        // WHEN
        // select 1(서비스 내부적 검사), insert query 1
        Board user111 = boardService.join(board, member.getName());


        // THEN
        // query 0
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
                .build();
        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
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
        roleRepository.save(role);

        // insert query 1
        boardRepository.save(board);


        em.flush();
        em.clear();


        // WHEN
        // select 3, update 1(내부적)
        Board findBoard = boardService.getBoard(board.getId(), member.getName());

        em.flush();
        em.clear();

        // THEN
        Assertions.assertThat(findBoard.getViews()).isEqualTo(1L);

    }

    @Test
    void getBoardList() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .build();
        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
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

        // insert query 5
        memberRepository.save(member);
        roleRepository.save(role);
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);


        em.flush();
        em.clear();


        // WHEN
        // query 3
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
                .build();
        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
                .build();

        // insert query 2
        memberRepository.save(member);
        roleRepository.save(role);


        // insert query 15
        for(var i=0; i<15; i++){
            Board board = Board.builder()
                    .views(0L)
                    .createdAt(LocalDateTime.now())
                    .contents("dasdas")
                    .title("dasdas")
                    .member(member)
                    .build();

            boardRepository.save(board);
        }


        em.flush();
        em.clear();

        // query 1
        List<Board> boardListAll = boardService.getBoardListAll();

        Assertions.assertThat(boardListAll.size()).isEqualTo(10);

    }

    @Test
    void setBoard() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .build();
        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
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
        roleRepository.save(role);

        // insert query 1
        boardRepository.save(board);


        em.flush();
        em.clear();


        Board updateBoard = Board.builder()
                .title("hello")
                .contents("world")
                .member(member)
                .id(board.getId())
                .build();

        // WHEN
        // query 3(select 2, update 1)
        // member의 findById는 role과 일대일 양방향 관계로 강제 로드(eager fetch)
        boardService.setBoard(updateBoard);

        em.flush();
        em.clear();

        // THEN
        // query 1
        Board isUpdatedBoard = boardRepository.findById(updateBoard.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND));

        Assertions.assertThat(updateBoard.getTitle()).isEqualTo(isUpdatedBoard.getTitle());
        Assertions.assertThat(updateBoard.getContents()).isEqualTo(isUpdatedBoard.getContents());


        // => 일대일 양방향 관계에서 연관관계 주인이 아닌 쪽에서 값을 조회할 경우 lazy가 안먹고 쿼리를 2번 전송, find(findById)를 사용할 경우 조인한 쿼리를 1번 전송
        // => 강제 조인(eager fetch)이므로 연관관계에 있는 엔티티 모두 영속화
    }

    @Test
    void removeBoard() {
        // GIVEN
        Member member = Member.builder()
                .password("fdsfd")
                .email("dsfdsfds")
                .name("user111")
                .build();
        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
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
        roleRepository.save(role);

        // insert query 1
        boardRepository.save(board);


        em.flush();
        em.clear();


        // WHEN
        // query 3(select 2, delete 1)
        boardService.removeBoard(member.getName(), board.getId());


        em.flush();
        em.clear();



        // THEN
        // query 1
        try {
            Board board1 = boardRepository.findById(board.getId()).orElseThrow(RuntimeException::new);
        }catch (RuntimeException e){
            return;
        }


        Assertions.fail("게시글이 존재함, 삭제처리되지않음");
    }

    @Test
    void 변경감지테스트(){
        // GIVEN
        Member member = Member.builder()
                .name("Fsdfsdf")
                .email("fdsffs")
                .password("dfadf")
                .build();
        Board board = Board.builder()
                .title("fdsfsd")
                .contents("hello")
                .createdAt(LocalDateTime.now())
                .views(0L)
                .member(member)
                .build();

        // insert query 3
        memberRepository.save(member);
        boardRepository.save(board);



        // WHEN
        // update query X
        // 기존 값과 동일한 경우 변경 감지 X
        board.setContents("hello");

        em.flush();
        em.clear();


        // THEN
        // select query 1
        Board findBoard = boardRepository.findById(board.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND));

        Assertions.assertThat(findBoard.getContents()).isEqualTo("hello");

    }

    @Test
    void 일대일연관관계주인이아닌엔티티_쿼리실행테스트(){
        // GIVEN
        Member member = Member.builder()
                .name("Fsdfsdf")
                .email("fdsffs")
                .password("dfadf")
                .build();

        // insert query 3
        memberRepository.save(member);

        em.flush();
        em.clear();


        // WHEN
        // 2 query
        // select 쿼리 2번 실행 (member, role)
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));


        // THEN
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());

        // => 일대일 관계의 양방향 설정된 연관관계 주인이 아닌 쪽은 fetch lazy 조인이 먹히지 않는다

    }
}