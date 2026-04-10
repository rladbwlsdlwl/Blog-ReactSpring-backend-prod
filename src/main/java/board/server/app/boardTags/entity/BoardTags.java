package board.server.app.boardTags.entity;


import board.server.app.board.entity.Board;
import board.server.app.member.entity.Member;
import board.server.app.tags.entity.Tags;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board_tags_table")
public class BoardTags {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tags_id")
    private Tags tags;

    public static BoardTags of(Long boardId, Long tagId) {
        Board board = Board.builder().id(boardId).build();
        Tags tags = Tags.builder().id(tagId).build();

        return BoardTags.builder()
                .board(board)
                .tags(tags)
                .build();
    }
    public static BoardTags of(Long boardId, Long tagId, String name) {
        Board board = Board.builder().id(boardId).build();
        Tags tags = Tags.builder().id(tagId).name(name).build();

        return BoardTags.builder()
                .board(board)
                .tags(tags)
                .build();
    }

    public static BoardTags of(Long id, Long boardId, Long tagId, String tagName){
        Tags tags = Tags.builder().id(tagId).name(tagName).build();
        Board board = Board.builder().id(boardId).build();

        return BoardTags.builder()
                .id(id)
                .board(board)
                .tags(tags)
                .build();
    }
}
