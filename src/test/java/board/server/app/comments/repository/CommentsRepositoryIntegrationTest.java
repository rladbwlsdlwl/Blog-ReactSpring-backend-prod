package board.server.app.comments.repository;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.comments.entity.Comments;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
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
class CommentsRepositoryIntegrationTest {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
//                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();


        // insert 3
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        comments.setId(commentsRepository.save(comments).getId());



        // WHEN (select 1)
        int size = commentsRepository.findByBoard_IdInWithMemberOrderByCreatedAtAsc(List.of(board.getId())).size();

        //THEN
        Assertions.assertThat(size).isEqualTo(1);

    }



    @Test
    void deleteById(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();


        // insert 3
        memberRepository.save(member);
        boardRepository.save(board);
        commentsRepository.save(comments);


        // WHEN
        commentsRepository.deleteById(comments.getId());



        // THEN
        commentsRepository.findById(comments.getId()).ifPresent((c) -> {

            Assertions.fail("댓글이 정상적으로 지워지지 않았습니다.");

        });

    }
}