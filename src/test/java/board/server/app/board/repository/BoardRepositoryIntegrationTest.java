package board.server.app.board.repository;

import board.server.app.board.entity.Board;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class BoardRepositoryIntegrationTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;


    @Test
    void findById() {
        // GIVEN
        Member member = Member.builder()
                .name("aaaaaaaaa")
                .email("bbbbbb@aaa.aaa")
                .password("ccc")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("hello")
                .contents("world")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();




        // insert 2
        memberRepository.save(member);
        boardRepository.save(board);


        // WHEN
        // THEN
        try{

            boardRepository.findById(board.getId()).orElseThrow(() -> new RuntimeException("존재하지않는 게시글"));

        }catch (RuntimeException e){
            Assertions.fail("findById: 게시글 id를 찾을 수 없음");
        }



    }

    @Test
    void findByMember_name() {
        // GIVEN
        Member member = Member.builder()
                .name("aaaaaaaaa")
                .email("bbbbbb@aaa.aaa")
                .password("ccc")
                .build();
        Board board1 = Board.builder()
                .member(member)
                .title("hello")
                .contents("world")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();
        Board board2 = Board.builder()
                .views(0L)
                .createdAt(LocalDateTime.now())
                .contents("dasdas")
                .title("dasdas")
                .member(member)
                .build();

        // insert query 1
        memberRepository.save(member);

        // insert query 2
        boardRepository.save(board1);
        boardRepository.save(board2);



        // WHEN
        List<Board> boardList = boardRepository.findByMember_name(member.getName());


        // THEN
        for(Board findBoard: boardList){
            Assertions.assertThat(findBoard.getMember().getName()).isEqualTo(member.getName());
        }
    }

    @Test
    void findTop10ByOrderByCreatedAtDesc() {
        // 게시글 10개 내림차순 정렬 테스트

        // GIVEN
        List<Member> memberList = new ArrayList<>();
        List<Board> boardList = new ArrayList<>();


        // 2명의 회원
        Member member1 = Member.builder()
                .name("aaaaaaaaa")
                .email("bbbbbb@aaa.aaa")
                .password("ccc")
                .build();
        Member member2 = Member.builder()
                .name("bbbbbbbbbbb")
                .email("cccccccccc@aaa.aaa")
                .password("ddd")
                .build();





        // insert query 2
        memberRepository.save(member1);
        memberRepository.save(member2);


        // 11개의 게시글
        for(int i =0 ;i<11; i++){
            Board board = Board.builder()
                    .member(i%2==0 ? member1: member2)
                    .title("hello")
                    .contents("world")
                    .views(0L)
                    .createdAt(LocalDateTime.now())
                    .build();


            // insert query 15
            boardList.add(board);
            boardRepository.save(board);
        }




        // WHEN
        List<Board> findBoardList = boardRepository.findTop10ByOrderByCreatedAtDescWithMember(PageRequest.of(0, 10, Sort.by("createdAt").descending()));


        // THEN
        Assertions.assertThat(findBoardList.size()).isEqualTo(10);
    }




    // ------------------------------------
    // 번외 테스트

    @Test
    void 더티체킹_변경감지테스트(){
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

        // insert query 2
        memberRepository.save(member);
        boardRepository.save(board);



        // WHEN
        // update query X
        // 기존 값과 동일한 경우 변경 감지 X
        board.setContents("hello");



        // THEN
        // select query 1
        Board findBoard = boardRepository.findById(board.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND));

        Assertions.assertThat(findBoard.getContents()).isEqualTo("hello");
    }

    // -------------------------------------
}