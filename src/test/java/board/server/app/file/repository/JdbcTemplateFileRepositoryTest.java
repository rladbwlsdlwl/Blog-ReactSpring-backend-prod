package board.server.app.file.repository;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.enums.RoleType;
import board.server.app.file.entity.FileEntity;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class JdbcTemplateFileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;



    @Test
    void saveAll() {
        // GIVEN
        Member member = Member.builder()
                .email("dasaasdas")
                .name("Dadsada")
                .password("dasdas")
                .build();
        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
                .build();
        Board board = Board.builder()
                .title("Dasda")
                .contents("dsada")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .member(member)
                .build();

        member.setId(memberRepository.save(member).getId());
        role.setId(roleRepository.save(role).getId());
        board.setId(boardRepository.save(board).getId());


        // WHEN

    }

    @Test
    void findByBoard_Id() {
    }

    @Test
    void findTop1ByBoard_Id() {
    }

    @Test
    void findFirstImageByBoardIdIn() {
    }

    @Test
    void findByOriginalFilenameAndCurrentFilename() {
    }

    @Test
    void deleteByCurrentFilenameIn() {
    }

    @Test
    void deleteByBoard_Id() {
    }
}