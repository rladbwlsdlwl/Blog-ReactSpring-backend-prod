package board.server.app.likes.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Likes {
    private Long id;
    private Long author; // fk - member(id), 좋아요를 누른 회원(liked_member_id)
    private Long postId; // fk - board(id)
}
