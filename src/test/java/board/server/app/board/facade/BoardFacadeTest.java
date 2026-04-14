package board.server.app.board.facade;

import board.server.app.board.entity.Board;
import board.server.app.board.service.BoardService;
import board.server.app.boardTags.service.BoardTagsService;
import board.server.app.file.service.FileService;
import board.server.app.member.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BoardFacadeTest {
    @InjectMocks
    BoardFacade boardFacade;

    @Mock
    BoardService boardService;

    @Mock
    FileService fileService;

    @Mock
    BoardTagsService boardTagsService;


    Member member;
    Board board;
    List<MultipartFile> filelist;
    List<String> taglist;


    @BeforeEach
    void init(){
        member = Member.builder().id(2L).build();
        board = Board.builder().id(1L).member(member).build();
        filelist = List.of(Mockito.mock(MultipartFile.class));
        taglist = List.of("테스트");
    }


    @DisplayName("실패 - 게시글 작성 실패 케이스")
    @Test
    void write_fail_board() throws IOException {
        // GIVEN
        // WHEN
        Mockito.doThrow(new RuntimeException("게시글 실패 예외"))
                .when(boardService)
                .join(Mockito.any());


       RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            boardFacade.write(board, filelist, taglist);
        });

        // THEN
        Mockito.verify(boardService, Mockito.times(1)).join(Mockito.any());

        Mockito.verify(fileService, Mockito.never()).upload(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(boardTagsService, Mockito.never()).join(Mockito.any(), Mockito.any());

    }

    @DisplayName("실패 - 파일 작성 실패 케이스")
    @Test
    void write_fail_file() throws IOException {
        // GIVEN
        Mockito.when(boardService.join(Mockito.any())).thenReturn(board);


        // WHEN
        Mockito.doThrow(new RuntimeException("게시글 실패 예외"))
                .when(fileService)
                .upload(Mockito.any(), Mockito.any(), Mockito.any());


        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            boardFacade.write(board, filelist, taglist);
        });

        // THEN
        Mockito.verify(boardService, Mockito.times(1)).join(Mockito.any());

        Mockito.verify(fileService, Mockito.times(1)).upload(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(boardTagsService, Mockito.never()).join(Mockito.any(), Mockito.any());

    }

    @DisplayName("실패 - 해시태그 작성 실패 케이스")
    @Test
    void write_fail_hashtag() throws IOException {
        // GIVEN
        Mockito.when(boardService.join(Mockito.any())).thenReturn(board);

        // WHEN
        Mockito.doThrow(new RuntimeException("게시글 실패 예외"))
                .when(boardTagsService)
                .join(Mockito.any(), Mockito.any());



        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            boardFacade.write(board, filelist, taglist);
        });


        // THEN
        Mockito.verify(boardService, Mockito.times(1)).join(Mockito.any());

        Mockito.verify(fileService, Mockito.times(1)).upload(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(boardTagsService, Mockito.times(1)).join(Mockito.any(), Mockito.any());

    }
}