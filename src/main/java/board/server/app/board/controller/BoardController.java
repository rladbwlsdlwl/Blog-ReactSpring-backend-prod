package board.server.app.board.controller;


import board.server.app.board.dto.BoardDto;
import board.server.app.board.dto.BoardRequestDto;
import board.server.app.board.dto.BoardResponseDto;
import board.server.app.board.dto.BoardResponseHomeDto;
import board.server.app.board.entity.Board;
import board.server.app.board.facade.BoardFacade;
import board.server.app.board.service.BoardService;
import board.server.app.boardTags.dto.BoardTagsRequestDto;
import board.server.app.boardTags.dto.BoardTagsRequestPatchDto;
import board.server.app.member.entity.Member;
import board.server.config.jwt.CustomUserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class BoardController {
    private final BoardService boardService;
    private final BoardFacade boardFacade;

    @Autowired
    public BoardController(BoardService boardService, BoardFacade boardFacade) {
        this.boardService = boardService;
        this.boardFacade = boardFacade; // POST, PATCH
    }


    @GetMapping
    public ResponseEntity<Object> home(@RequestParam(value = "lastId", required = false) Long lastId){


        Slice<Board> boardSlice = boardService.getBoardListAll(lastId);
        boolean hasNext = boardSlice.hasNext();

        List<BoardDto> data = boardSlice.getContent()
                .stream().map(BoardDto:: new).toList();

        BoardResponseHomeDto boardResponseDto = new BoardResponseHomeDto(data, hasNext);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

    @GetMapping("/{name}/{boardId}")
    public ResponseEntity<BoardDto> readBoard(@PathVariable String name,
                                              @PathVariable Long boardId){
        Board board = boardService.getBoard(boardId, name);

        BoardDto boardDto = new BoardDto(board, name);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardDto);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> readBoards(@PathVariable String name,
                                             @RequestParam int pageNum){

        Page<Board> boardPage= boardService.getBoardList(name, pageNum);

        Long totalElement = boardPage.getTotalElements();
        List<Board> boardList = boardPage.getContent();

        BoardResponseDto boardResponseDto = new BoardResponseDto(boardList, totalElement);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

    @PostMapping("/{name}")
    public ResponseEntity<Object> saveBoard(@AuthenticationPrincipal CustomUserDetail customUserDetail,
                                            @PathVariable String name,
                                            // board
                                            @RequestPart(value = "board") @Valid BoardRequestDto boardRequestDto,
                                            // file
                                            @RequestPart(value = "file", required = false) List<MultipartFile> multipartFileList,
                                            // hashtag
                                            @RequestPart(value = "hashtag", required = false) @Valid BoardTagsRequestDto boardTagsRequestDto) throws IOException {


        List<MultipartFile> filelist = multipartFileList == null ? new ArrayList<>() : multipartFileList;
        Long userId = customUserDetail.getId();

        String title = boardRequestDto.getTitle(), contents = boardRequestDto.getContents();

        Member member = Member.builder()
                .id(userId)
                .build();
        Board board = Board.builder()
                .title(title)
                .contents(contents)
                .views(0L)
                .createdAt(LocalDateTime.now())
                .member(member)
                .build();


        // tagdto remove
        Long id = boardFacade.write(board, filelist, boardTagsRequestDto.getName());
        BoardDto boardResponseDto = BoardDto.builder().id(id).build();


        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

    @PatchMapping("/{name}/{boardId}")
    public ResponseEntity<?> changeBoard(@AuthenticationPrincipal CustomUserDetail customUserDetail,
                                         @PathVariable("name") String name,
                                         @PathVariable("boardId") Long boardId,
                                         // board
                                         @RequestPart(value="board") @Valid BoardRequestDto boardRequestDto,
                                         // file
                                         @RequestPart(value = "file", required = false) List<MultipartFile> multipartFileList,
                                         @RequestPart(value = "removeFilenameList", required = false) List<MultipartFile> removeFilenameList,
                                         // hashtag
                                         @RequestPart(value="hashtag") @Valid BoardTagsRequestPatchDto boardTagsRequestPatchDto) throws IOException {

        // 파일 전처리
        List<MultipartFile> filelist = multipartFileList == null ? new ArrayList<>() : multipartFileList;
        List<MultipartFile> removefilelist = removeFilenameList == null ? new ArrayList<>() : removeFilenameList;

        // filename은 currentFilename과 동일
        List<String> removeFilename = new ArrayList<>();
        for(MultipartFile filename : removefilelist){
            removeFilename.add(new String(filename.getBytes()));
        }

        // 게시글 전처리
        Long userId = customUserDetail.getId();
        String title = boardRequestDto.getTitle();
        String contents = boardRequestDto.getContents();

        Member member = Member.builder()
                .id(userId)
                .build();
        Board board = Board.builder()
                .id(boardId)
                .member(member)
                .title(title)
                .contents(contents)
                .build();

        // 태그 전처리
        List<String> tagnameList = boardTagsRequestPatchDto.getName();

        Long id = boardFacade.update(board, removeFilename, filelist, tagnameList);
        BoardDto boardResponseDto = BoardDto.builder().id(id).build();

        return ResponseEntity.status(HttpStatus.OK).body(boardResponseDto);
    }

    @DeleteMapping("/{name}/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable("name") String name,
                                         @AuthenticationPrincipal CustomUserDetail customUserDetail,
                                         @PathVariable("boardId") Long boardId){
        Long userId = customUserDetail.getId();

        Long removeBoardId = boardService.removeBoard(userId, boardId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
