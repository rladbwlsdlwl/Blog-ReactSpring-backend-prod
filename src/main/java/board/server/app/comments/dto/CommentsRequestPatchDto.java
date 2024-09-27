package board.server.app.comments.dto;

import board.server.app.comments.entity.Comments;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsRequestPatchDto {
    @NotNull
    private Long author;
    @NotNull
    private String contents;
    private Long id;


    public static Comments of(CommentsRequestPatchDto commentsRequestPatchDto){
        return Comments.builder()
                .id(commentsRequestPatchDto.getId())
                .author(commentsRequestPatchDto.getAuthor())
                .contents(commentsRequestPatchDto.getContents())
                .build();
    }
}
