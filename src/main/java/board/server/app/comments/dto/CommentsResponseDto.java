package board.server.app.comments.dto;

import board.server.app.comments.entity.Comments;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
public class CommentsResponseDto {
    private Long id;
    private String contents;
    private String created_at;

    private String name; // username
    private Long board_id;
    private Long parent_id;
    private Long author;
    public static CommentsResponseDto of(Comments comments){
        Long parentId = comments.getComments() != null? comments.getComments().getId(): 0;

        return CommentsResponseDto.builder()
                .id(comments.getId())
                .contents(comments.getContents())
                .created_at(dateFormatter(comments.getCreatedAt()))
                .name(comments.getMember().getName())
                .board_id(comments.getBoard().getId())
                .parent_id(parentId)
                .author(comments.getMember().getId())
                .build();
    }

    static private String dateFormatter(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }
}
