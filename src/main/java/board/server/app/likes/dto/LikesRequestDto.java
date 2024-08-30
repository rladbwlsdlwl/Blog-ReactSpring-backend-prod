package board.server.app.likes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikesRequestDto {
    @NotNull
    private final Long author; // 좋아요 누른 회원 (member_id)
    @NotNull
    private final Long boardId; // 작성한 게시글 (board_id)
}
