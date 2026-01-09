package board.server.app.comments.dto;

import board.server.app.comments.entity.Comments;
import board.server.app.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentsRequestPatchDto {
    @NotNull
    private String contents;
}
