package board.server.app.likes.dto;

import board.server.app.likes.entity.Likes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LikesResponseDto {
    private Long id;
    private Long author;
    private Long postId;

    public static LikesResponseDto of(Likes likes){
        return LikesResponseDto.builder()
                .id(likes.getId())
                .author(likes.getMember().getId())
                .postId(likes.getBoard().getId())
                .build();
    }
}
