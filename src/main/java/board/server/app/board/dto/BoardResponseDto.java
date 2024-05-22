package board.server.app.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String title;
    private String contents;
    private Long userId;
}
