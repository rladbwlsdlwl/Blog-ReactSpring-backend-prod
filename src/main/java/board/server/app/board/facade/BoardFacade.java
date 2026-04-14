package board.server.app.board.facade;


import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.board.service.BoardService;
import board.server.app.boardTags.service.BoardTagsService;
import board.server.app.file.service.FileService;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BoardFacade {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private FileService fileService;
    @Autowired
    private BoardTagsService boardTagsService;


    // 게시글 POST 단일 진입점
    @Transactional
    public Long write(Board board, List<MultipartFile> filelist, List<String> taglist) throws IOException {
        Board newBoard = boardService.join(board);

        fileService.upload(filelist, newBoard.getId(), newBoard.getMember().getId());

        boardTagsService.join(taglist, newBoard.getId());

        return newBoard.getId();
    }

    // 게시글 PATCH 단일 진입점
    @Transactional
    public Long update(Board board, List<String> removeFilename, List<MultipartFile> filelist, List<String> tagnameList) throws IOException {
        Board findBoard = validatePresentBoard(board.getId());
        checkAuthorAndActiveUser(findBoard.getMember().getId(), board.getMember().getId());

        boardService.setBoard(board, findBoard);

        fileService.update(removeFilename, filelist, board.getId(), board.getMember().getId());

        boardTagsService.update(tagnameList, board.getId());

        return board.getId();
    }



    // 비즈니스 로직
    // 게시글 존재 검증
    private Board validatePresentBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND));
    }


    // 저자와 접속자 일치 여부 확인
    private void checkAuthorAndActiveUser(Long author, Long memberId) {
        if(!author.equals(memberId)) throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);
    }
}
