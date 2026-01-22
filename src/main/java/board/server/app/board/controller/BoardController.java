package board.server.app.board.controller;


import board.server.app.board.dto.BoardDto;
import board.server.app.board.dto.BoardRequestDto;
import board.server.app.board.dto.BoardResponseDto;
import board.server.app.board.dto.BoardResponseHomeDto;
import board.server.app.board.entity.Board;
import board.server.app.board.service.BoardService;
import board.server.app.member.entity.Member;
import board.server.config.jwt.CustomUserDetail;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
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
    public ResponseEntity<Object> saveBoard(@RequestBody @Valid BoardRequestDto boardRequestDto,
                                            @AuthenticationPrincipal CustomUserDetail customUserDetail,
                                            @PathVariable String name){
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


        Board savedBoard = boardService.join(board);

        BoardDto boardResponseDto = new BoardDto(savedBoard, name);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

    @PatchMapping("/{name}/{boardId}")
    public ResponseEntity<?> changeBoard(@RequestBody @Valid BoardRequestDto boardRequestDto,
                                         @AuthenticationPrincipal CustomUserDetail customUserDetail,
                                         @PathVariable("name") String name,
                                         @PathVariable("boardId") Long boardId){

        Long userId = customUserDetail.getId();

        String title = boardRequestDto.getTitle();
        String contents = boardRequestDto.getContents();

        Member member = Member.builder()
                .id(userId)
                .build();

        Board board = Board.builder()
                .member(member)
                .title(title)
                .contents(contents)
                .build();


        boardId = boardService.setBoard(board, boardId);

        BoardDto boardResponseDto = BoardDto.builder()
                .id(boardId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
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
