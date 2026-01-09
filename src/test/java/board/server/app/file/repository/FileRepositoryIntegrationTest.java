package board.server.app.file.repository;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.file.entity.FileEntity;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@Transactional
class FileRepositoryIntegrationTest {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private CustomFileRepository customFileRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;



    @Test
    void saveAll() {
        // GIVEN
        List<FileEntity> fileList = new ArrayList<>();

        Member member = Member.builder()
                .email("dasaasdas")
                .name("Dadsada")
                .password("dasdas")
                .build();
        Board board = Board.builder()
                .title("Dasda")
                .contents("dsada")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .member(member)
                .build();



        // insert 2
        member.setId(memberRepository.save(member).getId());
        board.setId(boardRepository.save(board).getId());



        // WHEN
        for(int i =0; i<5; i++) {

            FileEntity fileEntity = FileEntity.builder()
                    .board(board)
                    .originalFilename("helloworld")
                    .currentFilename("dsakjdasiofjwea"+i)
                    .build();

            fileList.add(fileEntity);
        }


        // insert 5 (batch update로 인해 쿼리 안보임)
        customFileRepository.saveAll(fileList);




        // THEN (select 1)
        List<FileEntity> findFileList = fileRepository.findByBoard_Id(board.getId());


        Assertions.assertThat(findFileList.size()).isEqualTo(5);
    }

    @Test
    void findByBoard_Id() {
    }

    @Test
    void findTop1ByBoard_Id() {
    }

    @Test
    void findFirstImageByBoardIdInWithBoard() {
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