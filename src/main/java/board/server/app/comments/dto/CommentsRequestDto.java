package board.server.app.comments.dto;


import board.server.app.board.entity.Board;
import board.server.app.comments.entity.Comments;
import board.server.app.member.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommentsRequestDto {
    @NotNull
    private Long parentId; // 0 OR comment's id
    @NotNull
    private String contents;
}
