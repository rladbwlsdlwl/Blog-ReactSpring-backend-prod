package board.server.app.board.controller;


import board.server.app.board.dto.BoardRequestDto;
import board.server.app.board.dto.BoardResponseDto;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.getBoardListAll());
    }

    @GetMapping("/{username}/{boardId}")
    public ResponseEntity<BoardResponseDto> readBoard(@PathVariable String username, @PathVariable Long boardId){
        Board board = boardService.getBoard(boardId, username);

        Long id = board.getId(), author = board.getAuthor();
        String title = board.getTitle(), contents = board.getContents();

        BoardResponseDto boardDto = new BoardResponseDto(id, title, contents, author);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardDto);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Object> readBoards(@PathVariable String username){
        List<Board> boardList = boardService.getBoardList(username);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardList);
    }

    @PostMapping("/{username}")
    public ResponseEntity<Object> saveBoard(@RequestBody @Valid BoardRequestDto boardRequestDto, @PathVariable String username){
        Long userId = boardRequestDto.getAuthor();
        String title = boardRequestDto.getTitle(), contents = boardRequestDto.getContents();

        Board board = Board.builder()
                .title(title)
                .contents(contents)
                .author(userId)
                .build();

        Long id = boardService.join(board, username);

        board.setId(id);
        BoardResponseDto boardResponseDto = new BoardResponseDto(id, title, contents, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

}
