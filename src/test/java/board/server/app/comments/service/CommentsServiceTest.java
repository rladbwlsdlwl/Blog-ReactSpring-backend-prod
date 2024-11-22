package board.server.app.comments.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.comments.dto.CommentsResponseDto;
import board.server.app.comments.entity.Comments;
import board.server.app.comments.repository.CommentsRepository;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
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
                .build();

        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());


        // WHEN
        Map<Long, List<CommentsResponseDto>> commentsList = commentsService.getComments(List.of(board.getId()));

        // THEN
        Assertions.assertThat(commentsList.get(board.getId()).size()).isEqualTo(0);

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
                .build();
        Comments comments = Comments.builder()
                .contents("Dasdsa")
                .board(board)
                .member(member)
                .build();
        Comments reply = Comments.builder()
                .contents("Dasdsa")
                .board(board)
                .member(member)
                .comments(comments)
                .build();


        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        comments.setId(commentsRepository.save(comments).getId());
        reply.setId(commentsRepository.save(reply).getId());

        // WHEN
        Map<Long, List<CommentsResponseDto>> commentsList = commentsService.getComments(List.of(board.getId()));

        // THEN
        Assertions.assertThat(commentsList.get(board.getId()).size()).isEqualTo(2);

    }
}