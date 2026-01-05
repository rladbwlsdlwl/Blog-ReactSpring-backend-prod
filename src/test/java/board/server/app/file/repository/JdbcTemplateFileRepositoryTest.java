package board.server.app.file.repository;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.enums.RoleType;
import board.server.app.file.entity.FileEntity;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

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
    void findFirstImageByBoardIdInWithBoard() {

        List<Long> boardIdList = List.of(3L, 15L);

        List<FileEntity> fileEntityList = fileRepository.findFirstImageByBoardIdInWithBoard(boardIdList);

        // 15L만 파일 존재
        Assertions.assertThat(fileEntityList.size()).isEqualTo(1);


        // File - Board - Member - id 추가 쿼리 전송안하는지 확인
        // Board 15L 을 작성한 유저는 18L
        Assertions.assertThat(fileEntityList.get(0).getBoard().getMember().getId()).isEqualTo(18L);

        // File - Board - title

        String title = fileEntityList.get(0).getBoard().getTitle();

        Assertions.assertThat(title).isEqualTo(title);
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