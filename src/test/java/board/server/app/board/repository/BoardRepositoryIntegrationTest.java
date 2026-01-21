package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        List<Board> findBoardList = boardRepository.findTop10ByOrderByCreatedAtDescWithMember(PageRequest.of(0, 10));


        // THEN
        Assertions.assertThat(findBoardList.size()).isEqualTo(10);
    }

    @Test
    void findByLessThanIdOrderByIdDescWithMember(){
        // 게시글 페이지네이션 테스트
        // 커서 기반 페이지네이션 (Non-Offset(id), Slice)

        // GIVEN
        Member member = Member.builder()
                .name("hellozzzzzzzzzzzz")
                .email("hellozzzzzzzzzzz@aaa.aaa")
                .roleType(RoleType.MEMBER)
                .password("adfkjgsio fjiafad")
                .build();

        memberRepository.save(member);


        // 게시글 22개 생성
        for(var i=0 ;i<22; i++){
            Board board = Board.builder()
                    .title("dadada")
                    .contents("dsadasdas")
                    .member(member)
                    .views(0L)
                    .createdAt(LocalDateTime.now())
                    .build();

            boardRepository.save(board);
        }


        boolean stopped = false;
        int pageSize = 10;


        Pageable pageable = PageRequest.of(0, pageSize);

        // WHEN
        List<Board> contents = boardRepository.findByLessThanIdInitOrderByIdDescWithMember(pageable).getContent();
        Long lastId = contents.get(contents.size() - 1).getId();


        List<Board> boardListDuplicated = new ArrayList<>();
        while(!stopped){
            // Slice -> getContents, hasNext
            // pageable -> page number (offset), page size (limit)
            Slice<Board> boardList = boardRepository.findByLessThanIdOrderByIdDescWithMember(lastId, pageable);
            contents = boardList.getContent();


            // 페이징 검사
            // contents <= page size
            Assertions.assertThat(contents.size()).isLessThanOrEqualTo(boardList.getSize());


            // 유니크한 리스트 값 검증
            List<Long> boardIdList = contents.stream().map(Board::getId).collect(Collectors.toList());
            Assertions.assertThat(boardIdList).doesNotHaveDuplicates();

            // 리스트 간 유니크 검증
            Assertions.assertThat(boardListDuplicated.stream().map(Board::getId)).doesNotContainAnyElementsOf(boardIdList);


            lastId = contents.get(contents.size() - 1).getId();
            stopped = !boardList.hasNext();

            boardListDuplicated.addAll(contents);
        }
    }


    @Test
    void findByLessThanIdInitOrderByIdDescWithMember(){

        // GIVEN
        Member member = Member.builder()
                .name("dasdkfoasf")
                .password("Dasfjdsaiofjwe3321312")
                .roleType(RoleType.MEMBER)
                .email("daijer@aaa.aaa")
                .build();

        memberRepository.save(member);


        // 게시글 8개 생성
        for(var i =0;i <8; i++){
            Board board = Board.builder()
                    .title("Dasasdsadad")
                    .contents("saddasdasdas")
                    .createdAt(LocalDateTime.now())
                    .views(0L)
                    .member(member)
                    .build();

            boardRepository.save(board);
        }





        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Board> boardSliceList = boardRepository.findByLessThanIdInitOrderByIdDescWithMember(pageable);



        // 내림차순 검증
        Assertions.assertThat(boardSliceList.getContent()).isSortedAccordingTo(Comparator.comparing(Board::getId).reversed());


        // 독립적인 값 검증
        Assertions.assertThat(boardSliceList.getContent().stream().map(Board::getId).toList())
                .doesNotHaveDuplicates();

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