package board.server.app.comments.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.comments.dto.CommentsResponseDto;
import board.server.app.comments.dto.CommentsResponsePatchDto;
import board.server.app.comments.entity.Comments;
import board.server.app.comments.repository.CommentsRepository;
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

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Transactional
@SpringBootTest
class CommentsServiceTest {

    @Autowired
    private CommentsService commentsService;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager em;


    @Test
    void 게시판아이디로_댓글읽기1(){
        // GIVEN
        Member member = Member.builder()
                .password("DAd")
                .email("DASda")
                .name("SAdas")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("SAdasd")
                .contents("DAasda")
                .createdAt(LocalDateTime.now())
                .views(0L)
                .build();

        // query 3
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());


        em.flush();
        em.clear();


        // WHEN
        // query 1
        Map<Long, List<CommentsResponseDto>> commentsList = commentsService.getComments(List.of(board.getId()));

        // THEN
        Assertions.assertThat(commentsList.get(board.getId()).size()).isEqualTo(0);

        // => 해당하는 댓글이 존재하지 않음
    }

    @Test
    void 게시판아이디로_댓글읽기2(){
        // GIVEN
        Member member = Member.builder()
                .password("DAd")
                .email("DASda")
                .name("SAdas")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("SAdasd")
                .contents("DAasda")
                .createdAt(LocalDateTime.now())
                .views(0L)
                .build();
        Comments comments = Comments.builder()
                .contents("Dasdsa")
                .board(board)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();
        Comments reply = Comments.builder()
                .contents("Dasdsa")
                .board(board)
                .member(member)
                .comments(comments)
                .createdAt(LocalDateTime.now())
                .build();


        // query 5
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        comments.setId(commentsRepository.save(comments).getId());
        reply.setId(commentsRepository.save(reply).getId());

        em.flush();
        em.clear();


        // WHEN
        // query 2
        Map<Long, List<CommentsResponseDto>> commentsList = commentsService.getComments(List.of(board.getId()));

        // THEN
        Assertions.assertThat(commentsList.get(board.getId()).size()).isEqualTo(2);

        // => 해당하는 댓글이 존재함, comments.member 초기화, member의 role 일대일 양방향 연관관계 주인이 아님(fetch eager loading)
    }

    @Test
    void 댓글추가(){
        // GIVEN
        Member member = Member.builder()
                .password("DAd")
                .email("DASda")
                .name("SAdas")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("SAdasd")
                .contents("DAasda")
                .createdAt(LocalDateTime.now())
                .views(0L)
                .build();
        Comments comments = Comments.builder()
                .contents("sdadas")
                .comments(null)
                .member(member)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();

        // query 3
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());


        em.flush();
        em.clear();


        // WHEN
        // query 3 (select 2, insert 1)
        Comments savedComments = commentsService.setComments(comments);


        // THEN
        // query 0
        Assertions.assertThat(savedComments.getId()).isEqualTo(comments.getId());
        Assertions.assertThat(savedComments.getMember().getId()).isEqualTo(member.getId());
        Assertions.assertThat(savedComments.getBoard().getId()).isEqualTo(board.getId());
    }

    @Test
    void 댓글수정(){
        // GIVEN
        Member member = Member.builder()
                .password("DAd")
                .email("DASda")
                .name("SAdas")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("SAdasd")
                .contents("DAasda")
                .createdAt(LocalDateTime.now())
                .views(0L)
                .build();
        Comments comments = Comments.builder()
                .contents("sdadas")
                .comments(null)
                .member(member)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();

        // query 4
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        commentsRepository.save(comments);


        em.flush();
        em.clear();


        // WHEN
        // query 2(select 1, update 1)
        Comments changedComments = Comments.builder()
                .id(comments.getId())
                .contents("hello world")
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();


        commentsService.updateComments(changedComments);

        em.flush();
        em.clear();


        // THEN
        // query 1
        Comments findComments = commentsRepository.findById(changedComments.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.COMMENTS_NO_PERMISSION));

        Assertions.assertThat(findComments.getContents()).isEqualTo(changedComments.getContents());
    }

    @Test
    void 댓글삭제(){
        // GIVEN
        Member member = Member.builder()
                .password("DAd")
                .email("DASda")
                .name("SAdas")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("SAdasd")
                .contents("DAasda")
                .createdAt(LocalDateTime.now())
                .views(0L)
                .build();
        Comments comments = Comments.builder()
                .contents("sdadas")
                .comments(null)
                .member(member)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();

        // query 4
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        commentsRepository.save(comments);


        em.flush();
        em.clear();


        // WHEN
        // query 4(select 3, delete 1)
        commentsService.removeComments(comments.getId(), comments.getMember().getId());


        // THEN
        // query 1

        em.flush();
        em.clear();
        try{
            commentsRepository.findById(comments.getId()).orElseThrow(() -> new RuntimeException(""));
        }catch (RuntimeException e){
            return ;
        }


        Assertions.fail("댓글이 삭제되지 않음");

        // => memberRepository의 findById는 내부적으로 양방향 설정된 엔티티인 role을 join fetch 하여 로드함
    }

}