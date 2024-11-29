package board.server.app.likes.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.likes.dto.LikesResponseDto;
import board.server.app.likes.entity.Likes;
import board.server.app.likes.repository.LikesRepository;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LikesServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private LikesService likesService;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    void getLikesList() {
        // GIVEN
        // board1은 member1과 member2가 좋아요를 누름
        // board2는 member1이 좋아요를 누름
        Member member1 = Member.builder()
                .email("adasd")
                .name("dasdsa")
                .password("dsasa")
                .build();
        Member member2 = Member.builder()
                .email("adasd1")
                .name("dasdsa1")
                .password("dsasa")
                .build();
        Board board1 = Board.builder()
                .title("dasd")
                .contents("dsad")
                .member(member1)
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();
        Board board2 = Board.builder()
                .title("dasd")
                .contents("dsad")
                .member(member1)
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();


        // query 6
        member1.setId(memberRepository.save(member1).getId());
        member2.setId(memberRepository.save(member2).getId());
        board1.setId(boardRepository.save(board1).getId());
        board2.setId(boardRepository.save(board2).getId());


        em.flush();
        em.clear();

        // WHEN
        // query 3
        Likes likes1 = Likes.builder()
                .member(member1)
                .board(board1)
                .build();
        Likes likes2 = Likes.builder()
                .member(member2)
                .board(board1)
                .build();
        Likes likes3 = Likes.builder()
                .member(member1)
                .board(board2)
                .build();

        likes1.setId(likesRepository.save(likes1).getId());
        likes2.setId(likesRepository.save(likes2).getId());
        likes3.setId(likesRepository.save(likes3).getId());


        // query 1
        Map<Long, List<LikesResponseDto>> likeslist = likesService.getLikesList(List.of(board1.getId(), board2.getId()));


        // THEN
        Assertions.assertThat(likeslist.get(board1.getId()).size()).isEqualTo(2);
        Assertions.assertThat(likeslist.get(board2.getId()).size()).isEqualTo(1);
    }

    @Test
    void setLikes() {
        // GIVEN
        // 회원 1과 회원 2는 게시글1 좋아요를 눌렀다
        Member member1 = Member.builder()
                .email("adasd")
                .name("dasdsa")
                .password("dsasa")
                .build();
        Member member2 = Member.builder()
                .email("adasd1")
                .name("dasdsa1")
                .password("dsasa")
                .build();
        Board board1 = Board.builder()
                .title("dasd")
                .contents("dsad")
                .member(member1)
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();

        // query 5
        member1.setId(memberRepository.save(member1).getId());
        memberRepository.save(member2);
        board1.setId(boardRepository.save(board1).getId());



        // WHEN
        Likes likes1 = Likes.builder()
                .member(member1)
                .board(board1)
                .build();
        Likes likes2 = Likes.builder()
                .member(member2)
                .board(board1)
                .build();


        // query 4(select 2, insert 2)
        likesService.setLikes(likes1);
        likesService.setLikes(likes2);



        em.flush();
        em.clear();

        // THEN
        // query 1
        List<Likes> likeslist = likesRepository.findByBoard_Id(board1.getId());

        Assertions.assertThat(likeslist.size()).isEqualTo(2);
    }

    @Test
    void removeLikes() {
        // GIVEN
        // 회원 1과 회원 2는 게시글1 좋아요를 눌렀다
        // 회원 1은 좋아요를 취고한다
        Member member1 = Member.builder()
                .email("adasd")
                .name("dasdsa")
                .password("dsasa")
                .build();
        Member member2 = Member.builder()
                .email("adasd1")
                .name("dasdsa1")
                .password("dsasa")
                .build();
        Board board1 = Board.builder()
                .title("dasd")
                .contents("dsad")
                .member(member1)
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();

        // query 5
        member1.setId(memberRepository.save(member1).getId());
        memberRepository.save(member2);
        board1.setId(boardRepository.save(board1).getId());


        em.flush();
        em.clear();

        // WHEN
        Likes likes1 = Likes.builder()
                .member(member1)
                .board(board1)
                .build();
        Likes likes2 = Likes.builder()
                .member(member2)
                .board(board1)
                .build();


        // query 2
        likes1.setId(likesRepository.save(likes1).getId());
        likes2.setId(likesRepository.save(likes2).getId());


        // query 2
        likesService.removeLikes(likes1);


        em.flush();
        em.clear();


        // THEN
        // query 1
        List<Likes> likeslist = likesRepository.findByBoard_Id(board1.getId());

        Assertions.assertThat(likeslist.size()).isEqualTo(1);
    }
}