package board.server.app.board.facade;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.board.service.BoardService;
import board.server.app.boardTags.service.BoardTagsService;
import board.server.app.file.service.FileService;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
class BoardFacadeIntegrationTest {

    @Autowired
    BoardFacade boardFacade;

    @Autowired
    BoardService boardService;

    @Autowired
    FileService fileService;

    @Autowired
    BoardTagsService boardTagsService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;




    Member member;
    Board board;

    @BeforeEach
    void init(){

        member = Member.builder()
                .name("helloworld123")
                .email("helloworld123@aaa.aaa")
                .build();
        board = Board.builder()
                .member(member)
                .title("dasdas")
                .contents("dsada rewrae")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);

    }


    @AfterEach
    void clean(){
        memberRepository.delete(member);
    }

    @DisplayName("실패 - 파일 작성 실패")
    @Test
    void write_fail_file() throws IOException {
        // GIVEN
        Member member = Member.builder()
                .name("helloworld")
                .email("helloworld@aaa.aaa")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("dasdas")
                .contents("dsada rewrae")
                .views(0L)
                .createdAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);



        // 파일 로직에서 예외사항 발생
        // 파일 타입 에러
        MultipartFile badFile = new MockMultipartFile(
                "file",
                "test.txt",   // ❌ 여기 때문에 validateFilesType에서 터짐
                "text/plain",
                "dummy".getBytes()
        );

        // WHEN
        // 게시글 작성 로직 실행
        BusinessLogicException businessLogicException = assertThrows(BusinessLogicException.class, () -> {

            boardFacade.write(board, List.of(badFile), List.of());

        });



        // THEN
        // 예외 코드 확인
        org.assertj.core.api.Assertions.assertThat(businessLogicException.getExceptionCode()).isEqualTo(CommonExceptionCode.FILE_TYPE_NOT_VALID);

        // 롤백 여부 확인
        // helloworld 유저가 작성한 글은 없어야 함 (이전 상태로 되돌림)
        List<Board> boardlist = boardRepository.findByMember_name(member.getName());


        // 게시글 없음 (전 상태로 복구)
        org.assertj.core.api.Assertions.assertThat(boardlist.size()).isEqualTo(0);
    }

}