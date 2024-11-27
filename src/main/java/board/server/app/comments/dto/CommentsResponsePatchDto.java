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
public class CommentsResponsePatchDto {
    private Long id;
    private String contents;
    private Long author;
    private String created_at;


    public static CommentsResponsePatchDto of(Comments comments){
        return CommentsResponsePatchDto.builder()
                .id(comments.getId())
                .contents(comments.getContents())
                .author(comments.getMember().getId())
                .created_at(dateFormatter(comments.getCreatedAt()))
                .build();
    }

    private static String dateFormatter(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(dateTimeFormatter);
    }
}
