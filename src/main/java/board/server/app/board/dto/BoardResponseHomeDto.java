package board.server.app.board.dto;

import board.server.app.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@AllArgsConstructor
@Getter
@Setter
public class BoardResponseHomeDto {
    private Long id;
    private String title;
    private String contents;
    private Long views;
    private String created_at;
    private Long memberId;
    private String username;

    public BoardResponseHomeDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.views = board.getViews();
        this.created_at = dateFormatter(board.getCreatedAt());
        this.memberId = board.getMember().getId();
        this.username = board.getMember().getName();
    }

    private String dateFormatter(LocalDateTime localDateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }
}
