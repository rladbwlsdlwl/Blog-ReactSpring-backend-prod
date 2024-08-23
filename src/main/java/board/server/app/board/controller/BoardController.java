package board.server.app.board.controller;


import board.server.app.board.dto.BoardRequestDto;
import board.server.app.board.dto.BoardResponseDto;
import board.server.app.board.dto.BoardResponseHomeDto;
import board.server.app.board.entity.Board;
import board.server.app.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> home(){
        List<BoardResponseHomeDto> boardList = boardService.getBoardListAll()
                .stream().map(BoardResponseHomeDto:: new).toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(boardList);
    }

    @GetMapping("/{name}/{boardId}")
    public ResponseEntity<BoardResponseDto> readBoard(@PathVariable String name, @PathVariable Long boardId){
        Board board = boardService.getBoard(boardId, name);

        Long id = board.getId(), author = board.getAuthor();
        String title = board.getTitle(), contents = board.getContents();

        BoardResponseDto boardDto = new BoardResponseDto(id, title, contents, author);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardDto);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> readBoards(@PathVariable String name){
        List<Board> boardList = boardService.getBoardList(name);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardList);
    }

    @PostMapping("/{name}")
    public ResponseEntity<Object> saveBoard(@RequestBody @Valid BoardRequestDto boardRequestDto, @PathVariable String name){
        Long userId = boardRequestDto.getAuthor();
        String title = boardRequestDto.getTitle(), contents = boardRequestDto.getContents();

        Board board = Board.builder()
                .title(title)
                .contents(contents)
                .author(userId)
                .build();

        Long id = boardService.join(board, name);

        board.setId(id);
        BoardResponseDto boardResponseDto = new BoardResponseDto(id, title, contents, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

    @PatchMapping("/{name}/{boardId}")
    public ResponseEntity<?> changeBoard(@RequestBody @Valid BoardRequestDto boardRequestDto,
                                         @PathVariable("name") String name,
                                         @PathVariable("boardId") Long boardId){
        String title = boardRequestDto.getTitle();
        String contents = boardRequestDto.getContents();
        Long author = boardRequestDto.getAuthor();

        Board board = Board.builder()
                .id(boardId)
                .username(name)
                .author(author)
                .title(title)
                .contents(contents)
                .build();


        boardId = boardService.setBoard(board);

        BoardResponseDto boardResponseDto = new BoardResponseDto(boardId, title, contents, author);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

    @DeleteMapping("/{name}/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable("name") String name, @PathVariable("boardId") Long boardId){
        Long removeBoardId = boardService.removeBoard(name, boardId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
