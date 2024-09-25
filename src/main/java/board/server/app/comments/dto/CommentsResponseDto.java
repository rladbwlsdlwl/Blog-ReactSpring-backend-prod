package board.server.app.comments.dto;

import board.server.app.comments.entity.Comments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CommentsResponseDto {
    private Long id;
    private Long parent_id;
    private Long author;
    private String name;
    private Long board_id;
    private String contents;
    private String created_at;

    public CommentsResponseDto(Comments comments){
        this.id = comments.getId();
        this.parent_id = comments.getParentId();
        this.author = comments.getAuthor();
        this.name = comments.getAuthorName();
        this.board_id = comments.getBoardId();
        this.contents = comments.getContents();
        this.created_at = dateFormatter(comments.getCreatedAt());
    }

    private String dateFormatter(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }
}
