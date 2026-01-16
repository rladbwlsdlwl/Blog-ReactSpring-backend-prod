package board.server.app.file.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.file.repository.CustomFileRepository;
import board.server.app.file.repository.FileRepository;
import board.server.app.member.entity.Member;
import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    FileService fileService;
    @Mock
    FileRepository fileRepository;
    @Mock
    CustomFileRepository customFileRepository;
    @Mock
    BoardRepository boardRepository;



    @Test
    void update_fail_boardId(){
        // GIVEN
        Long board_id = 5L;
        Long member_id = 1L;
        List<String> removeFilenameList = new ArrayList<>();
        List<MultipartFile> fileList = new ArrayList<>();


        // 존재하지 않는 게시글
        // Optional<T> = null
        Mockito.when(boardRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());


        // WHEN
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> fileService.update(removeFilenameList, fileList, board_id, member_id));


        // THEN
        Assertions.assertEquals(exception.getExceptionCode(), CustomExceptionCode.BOARD_NOT_FOUND);

        Mockito.verify(customFileRepository, Mockito.never()).saveAll(Mockito.anyList());
    }

    @Test
    void update_fail_author(){
        // GIVEN
        // 1L로 접속한 유저가 2L유저의 파일 수정 요청
        Long board_id = 5L;
        Long author = 2L;
        Long member_id = 1L;
        List<String> removeFilenameList = new ArrayList<>();
        List<MultipartFile> fileList = new ArrayList<>();


        Member member = Member.builder()
                .id(author)
                .build();
        Board board = Board.builder()
                .id(board_id)
                .member(member)
                .build();


        // author
        Mockito.when(boardRepository.findById(Mockito.any())).thenReturn(Optional.of(board));



        // member_id
        // WHEN
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> fileService.update(removeFilenameList, fileList, board_id, member_id));




        // THEN
        Assertions.assertEquals(exception.getExceptionCode(), CommonExceptionCode.FORBIDDEN);


        Mockito.verify(fileRepository, Mockito.never()).deleteByCurrentFilenameIn(Mockito.anyList());
        Mockito.verify(customFileRepository, Mockito.never()).saveAll(Mockito.anyList());
    }

    
    @Test
    void update_fail_currentFilename() {
        // GIVEN
        // 게시글 id와 파일 선두 이름 불일치로 인한 예외 상황
        List<String> removeFilelist = List.of("5_dasdasdasdasd.png", "15_addjsadasjndas.png");
        List<MultipartFile> afterFilelist = new ArrayList<>();
        Long board_id = 5L;
        Long member_id = 1L;

        Member member = Member.builder()
                .id(member_id)
                .build();
        Board board = Board.builder()
                .id(board_id)
                .member(member)
                .build();
        

        // validateBoardIdAndAuthor
        Mockito.when(boardRepository.findById(board_id)).thenReturn(Optional.of(board));
        
        // validateCurrentFilename
        // WHEN
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> fileService.update(removeFilelist, afterFilelist, board_id, member_id));
        
        
        // THEN
        Assertions.assertEquals(exception.getExceptionCode(), CommonExceptionCode.FORBIDDEN);

        Mockito.verify(customFileRepository, Mockito.never()).saveAll(Mockito.anyList());
        Mockito.verify(fileRepository, Mockito.never()).deleteByCurrentFilenameIn(Mockito.anyList());
    }
}