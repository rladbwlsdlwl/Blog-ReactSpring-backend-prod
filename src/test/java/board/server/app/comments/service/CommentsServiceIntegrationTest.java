package board.server.app.comments.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.comments.dto.CommentsResponseDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Transactional
@SpringBootTest
class CommentsServiceIntegrationTest {
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;


    @Test
    void getComments(){
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

        // query 4
        memberRepository.save(member);
        boardRepository.save(board);
        commentsRepository.save(comments);
        commentsRepository.save(reply);

        // WHEN
        // query 1
        Map<Long, List<CommentsResponseDto>> commentsList = commentsService.getComments(List.of(board.getId()));

        // THEN
        Assertions.assertThat(commentsList.get(board.getId()).size()).isEqualTo(2);
    }

    @Test
    void setComments(){
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

        // query 2
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());


        // WHEN
        // query 1
        Comments savedComments = commentsService.setComments(comments, board.getId(), 0L);


        // THEN
        // query 0
        Assertions.assertThat(savedComments.getId()).isEqualTo(comments.getId());
        Assertions.assertThat(savedComments.getMember().getId()).isEqualTo(member.getId());
        Assertions.assertThat(savedComments.getBoard().getId()).isEqualTo(board.getId());
    }

    @Test
    void updateComments(){
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
        commentsRepository.save(comments);



        // WHEN
        // 기존 댓글 수정
        Long changedId = comments.getId();

        Comments changedComments = Comments.builder()
                .contents("hello world")
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        // query 1
        commentsService.updateComments(changedComments, changedId);

        // THEN
        // query 1
        Comments findComments = commentsRepository.findById(changedId).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.COMMENTS_NO_PERMISSION));

        Assertions.assertThat(findComments.getContents()).isEqualTo(changedComments.getContents());
    }

    @Test
    void removeComments(){
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
        commentsRepository.save(comments);



        // WHEN
        commentsService.removeComments(comments.getId(), comments.getMember().getId());


        // THEN
        try{
            commentsRepository.findById(comments.getId()).orElseThrow(() -> new RuntimeException(""));
        }catch (RuntimeException e){
            return ;
        }


        Assertions.fail("댓글이 삭제되지 않음");
    }

}