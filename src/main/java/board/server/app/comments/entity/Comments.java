package board.server.app.comments.entity;

import board.server.app.board.entity.Board;
import board.server.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "COMMENTS_TABLE")
public class Comments {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 작성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comments comments; // 대댓글 부모 참조
}
