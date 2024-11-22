package board.server.app.board.dto;

import board.server.app.board.entity.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BoardResponseDto {
    private Long id;
    private String title;
    private String contents;
    private Long views;
    private String created_at;
    private Long memberId;

    public BoardResponseDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.views = board.getViews();
        this.created_at = dateFormatter(board.getCreated_at());
        this.memberId = board.getMember().getId();
    }

    private String dateFormatter(LocalDateTime localDateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }
}
