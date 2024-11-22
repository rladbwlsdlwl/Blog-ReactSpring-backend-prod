package board.server.app.comments.repository;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.comments.entity.Comments;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
@Transactional
class JdbcTemplateCommentsRepositoryTest {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 댓글저장확인(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
//                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();

        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        comments.setId(commentsRepository.save(comments).getId());

        //WHEN
        int size = commentsRepository.findByBoard_IdInWithMemberOrderByCreatedAtAsc(List.of(board.getId())).size();

        //THEN
        Assertions.assertThat(size).isEqualTo(1);

    }


    @Test
    void 제약조건위반댓글_대댓글(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
//                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();

        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        comments.setId(commentsRepository.save(comments).getId());


        // WHEN
        Comments comments1 = Comments.builder()
                .member(member)
                .board(board)
                .comments(Comments.builder().id(comments.getId()+100).build()) //id <- parent_id(refenences, foreign key) 존재하지않는 id값 push
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();


        // THEN
        try{
            commentsRepository.save(comments1);
        }catch(Exception e){
            return;
        }


        // 위 조건을 pass하면
        Assertions.fail("무결성제약조건 위반이 이루어지지 않음");
    }

    @Test
    void 제약조건위반댓글_존재하지않는유저(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
//                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();


        Long id = memberRepository.save(member).getId();


        // WHEN
        member.setId(id + 100);

        // THEN
        try{
            boardRepository.save(board);
            // commentsRepository.save(comments);
        }catch(Exception e){
            return;
        }



        // 위 조건을 pass하면
        Assertions.fail("무결성제약조건 위반이 이루어지지 않음");
    }

    @Test
    void 제약조건위반댓글_존재하지않는게시글(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
//                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();

        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());

        // WHEN
        board.setId(board.getId() + 100);

        // THEN
        try{
            commentsRepository.save(comments);
        }catch(Exception e){
            return;
        }



        // 위 조건을 pass하면
        Assertions.fail("무결성제약조건 위반이 이루어지지 않음");
    }


    @Test
    void 제약조건위반하지않는댓글(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
//                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();


        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        comments.setId(commentsRepository.save(comments).getId());



        Comments reply1 = Comments.builder()
                .board(board)
                .member(member)
                .comments(comments)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();
        Comments reply2 = Comments.builder()
                .board(board)
                .member(member)
                .comments(comments)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();

        // WHEN
        commentsRepository.save(reply1);
        commentsRepository.save(reply2);

        // THEN
        int size = commentsRepository.findByBoard_IdInWithMemberOrderByCreatedAtAsc(List.of(board.getId())).size();

        Assertions.assertThat(size).isEqualTo(3);

    }

    @Test
    void 댓삭시제약조건_하위댓글모두삭제(){
        // GIVEN
        Member member = Member.builder()
                .name("dsdsd")
                .email("sdasd")
                .password("DSad")
                .build();
        Board board = Board.builder()
                .title("sds")
                .contents("sdsdsd")
                .member(member)
                .build();
        Comments comments = Comments.builder()
                .board(board)
                .member(member)
//                .comments(null)
                .contents("hello world")
                .createdAt(LocalDateTime.now())
                .build();



        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());
        comments.setId(commentsRepository.save(comments).getId());


        Comments reply = Comments.builder()
                .board(board)
                .member(member)
                .comments(comments)
                .contents("SDads")
                .build();

        reply.setId(commentsRepository.save(reply).getId());

        // WHEN
        commentsRepository.deleteById(comments.getId());



        // THEN
        // 무결성제약조건으로 인해 하위 댓글 자동 삭제 처리
        commentsRepository.findById(comments.getId()).ifPresent(comment ->
                Assertions.fail("댓글 삭제 오류입니다")
        );
        commentsRepository.findById(reply.getId()).ifPresent(comment ->
                Assertions.fail("댓글 삭제 오류입니다")
        );

    }
}