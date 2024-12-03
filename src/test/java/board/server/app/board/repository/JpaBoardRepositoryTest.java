package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
import jakarta.persistence.EntityManager;
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
class JpaBoardRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager em;


    @Test
    void findById() {
    }

    @Test
    void findByMember_name() {
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

        // insert query 2
        memberRepository.save(member);
        roleRepository.save(role);

        // insert query 2
        boardRepository.save(board1);
        boardRepository.save(board2);



        em.flush();
        em.clear();


        // WHEN
        // query 1 (board 영속화, no fetch join)
        List<Board> boardList = boardRepository.findByMember_name(member.getName());


        // query 1 (한 회원의 게시글 리스트이므로 쿼리는 한번만 전송함, member는 일대일 양방향으로 role과 eager join 시행)
        for(Board findBoard: boardList){
            Assertions.assertThat(findBoard.getMember().getName()).isEqualTo(member.getName());
        }
    }

    @Test
    void findTop10ByOrderByCreatedAtDesc() {
    }
}