package board.server.app.comments.repository;

import board.server.app.comments.entity.Comments;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class JdbcTemplateCommentsRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplateCommentsRepository jdbcTemplateCommentsRepository;

    @Test
    void 댓글저장확인(){
        // GIVEN
        Comments comments = Comments.builder()
                .boardId(3L)
                .parentId(null)
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();

        //WHEN
        Long save = jdbcTemplateCommentsRepository.save(comments);
        int size = jdbcTemplateCommentsRepository.findByBoardId(3L).size();

        //THEN

        Assertions.assertThat(size).isEqualTo(1);

    }


    @Test
    void 제약조건위반댓글_대댓글(){
        // GIVEN
        Comments comments = Comments.builder()
                .boardId(3L)
                .parentId(null)
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();

        Long id1 = jdbcTemplateCommentsRepository.save(comments);

        Comments comments1 = Comments.builder()
                .boardId(3L)
                .parentId(id1+100) //id <- parent_id(refenences, foreign key) 존재하지않는 id값 push
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();


        //WHEN THEN
        try{
            jdbcTemplateCommentsRepository.save(comments1);
        }catch(Exception e){
            return;
        }


        // 위 조건을 pass하면
        Assertions.fail("무결성제약조건 위반이 이루어지지 않음");
    }

    @Test
    void 제약조건위반댓글_존재하지않는유저(){
        // GIVEN
        Comments comments = Comments.builder()
                .boardId(3L)
                .parentId(null)
                .contents("hello world")
                .author(100L)
                .createdAt(LocalDateTime.now())
                .build();

        //WHEN THEN
        try{
            jdbcTemplateCommentsRepository.save(comments);
        }catch(Exception e){
            return;
        }



        // 위 조건을 pass하면
        Assertions.fail("무결성제약조건 위반이 이루어지지 않음");
    }

    @Test
    void 제약조건위반댓글_존재하지않는게시글(){
        // GIVEN
        Comments comments = Comments.builder()
                .boardId(100L)
                .parentId(null)
                .contents("hello world")
                .author(100L)
                .createdAt(LocalDateTime.now())
                .build();


        //WHEN THEN
        try{
            jdbcTemplateCommentsRepository.save(comments);
        }catch(Exception e){
            return;
        }



        // 위 조건을 pass하면
        Assertions.fail("무결성제약조건 위반이 이루어지지 않음");
    }


    @Test
    void 제약조건위반하지않는댓글(){
        // GIVEN
        Comments comments = Comments.builder()
                .boardId(3L)
                .parentId(null)
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();

        Long save = jdbcTemplateCommentsRepository.save(comments);


        Comments comments1 = Comments.builder()
                .boardId(3L)
                .parentId(save)
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();

        Comments comments2 = Comments.builder()
                .boardId(3L)
                .parentId(save)
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();

        // WHEN
        jdbcTemplateCommentsRepository.save(comments1);
        jdbcTemplateCommentsRepository.save(comments2);

        // THEN
        int size = jdbcTemplateCommentsRepository.findByBoardId(3L).size();

        Assertions.assertThat(size).isEqualTo(3);

    }

    @Test
    void 댓삭시제약조건_하위댓글모두삭제(){
        // GIVEN
        Comments comments = Comments.builder()
                .boardId(3L)
                .parentId(null)
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();

        Long id = jdbcTemplateCommentsRepository.save(comments);


        Comments comments1 = Comments.builder()
                .boardId(3L)
                .parentId(id)
                .contents("hello world")
                .author(83L)
                .createdAt(LocalDateTime.now())
                .build();

        Long id1 = jdbcTemplateCommentsRepository.save(comments1);


        // WHEN
        jdbcTemplateCommentsRepository.delete(id);



        // THEN
        // 무결성제약조건으로 인해 하위 댓글 자동 삭제 처리
        jdbcTemplateCommentsRepository.findById(id).ifPresent(comment ->
                Assertions.fail("댓글 삭제 오류입니다")
        );
        jdbcTemplateCommentsRepository.findById(id1).ifPresent(comment ->
                Assertions.fail("댓글 삭제 오류입니다")
        );

    }
}