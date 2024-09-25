package board.server.app.comments.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class Comments {
    private Long id;
    private Long parentId; // 대댓글
    private Long author; // 댓글 작성자
    private String authorName; // 댓글 작성자 닉네임
    private Long boardId;
    private String contents;
    private LocalDateTime createdAt; // 작성일
}
