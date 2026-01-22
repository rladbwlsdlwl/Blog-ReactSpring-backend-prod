package board.server.app.board.dto;

import board.server.app.board.entity.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class BoardResponseDto {
    List<BoardDto> data;
    Long totalPage;
    Long totalElement;



    public BoardResponseDto(List<Board> boardList, Long totalElement){
        this.data = boardList.stream().map(BoardDto:: new).collect(Collectors.toList());
        this.totalElement = totalElement; // 총 레코드 수
        this.totalPage = totalElement/25 + 1; // (전체 레코드 수/limit 수) -> 총 offset 페이지 수
    }
}
