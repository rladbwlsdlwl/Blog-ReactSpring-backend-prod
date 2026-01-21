package board.server.app.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BoardResponseHomeDto {
    List<BoardDto> data;
    private boolean hasNext; // scroll data prevent? true: false
}
